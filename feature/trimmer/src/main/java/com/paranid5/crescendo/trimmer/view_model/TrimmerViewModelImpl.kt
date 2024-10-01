package com.paranid5.crescendo.trimmer.view_model

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import arrow.core.Either
import arrow.core.Tuple4
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.domain.waveform.WaveformRepository
import com.paranid5.crescendo.system.worker.trimmer.TrimmerWorkRequest
import com.paranid5.crescendo.system.worker.trimmer.TrimmerWorker
import com.paranid5.crescendo.trimmer.domain.player.PlayerStateChangedListener
import com.paranid5.crescendo.trimmer.domain.player.seekTenSecsBack
import com.paranid5.crescendo.trimmer.domain.player.seekTenSecsForward
import com.paranid5.crescendo.trimmer.domain.player.stopAndReleaseCatching
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.FileSaveDialogProperties
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackPositions
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackPositions.Companion.InitialPosition
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackProperties
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackProperties.Companion.InitialPitch
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackProperties.Companion.InitialSpeed
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.WaveformZoomProperties
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.WaveformZoomProperties.Companion.InitialZoomLevel
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.fold
import com.paranid5.crescendo.ui.foundation.getOrNull
import com.paranid5.crescendo.ui.foundation.toUiState
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.AsyncCondVar
import com.paranid5.crescendo.utils.extensions.sideEffect
import com.paranid5.crescendo.utils.extensions.toMediaItem
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import linc.com.amplituda.Amplituda
import linc.com.amplituda.callback.AmplitudaErrorListener
import java.io.File

@OptIn(UnstableApi::class)
internal class TrimmerViewModelImpl(
    @SuppressLint("StaticFieldLeak")
    private val appContext: Context,
    private val savedStateHandle: SavedStateHandle,
    private val tracksRepository: TracksRepository,
    private val waveformRepository: WaveformRepository,
    private val json: Json,
) : ViewModel(), TrimmerViewModel, StatePublisher<TrimmerState> {
    companion object {
        private const val StateKey = "state"
        private const val PlaybackUpdateCooldown = 500L
    }

    private var dataUpdatesJob: Job? = null
    private var playbackPositionOutOfBordersJob: Job? = null
    private var playPauseJob: Job? = null
    private var audioEffectsJob: Job? = null

    private val amplituda by lazy {
        Amplituda(appContext)
    }

    private val trackPlayer by lazy {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(C.USAGE_MEDIA)
            .build()

        ExoPlayer.Builder(appContext)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setPauseAtEndOfMediaItems(false)
            .build()
            .apply {
                addListener(
                    PlayerStateChangedListener(
                        onPlaybackLaunched = { onPlaybackLaunched() },
                        onCompletion = { onCompletion() },
                    )
                )

                repeatMode = ExoPlayer.REPEAT_MODE_OFF
            }
    }

    private val mutex = Mutex()

    private suspend fun <R> withPlayer(func: Player.() -> R) =
        mutex.withLock { withContext(Dispatchers.Main) { func(trackPlayer) } }

    private val resetPlaybackPosCondVar by lazy { AsyncCondVar() }

    override val stateFlow = savedStateHandle.getStateFlow(StateKey, TrimmerState())

    override fun updateState(func: TrimmerState.() -> TrimmerState) {
        savedStateHandle[StateKey] = func(state)
    }

    private fun updateWaveformProperties(func: WaveformZoomProperties.() -> WaveformZoomProperties) =
        updateState { copy(waveformProperties = func(waveformProperties)) }

    private fun updatePlaybackPositions(func: PlaybackPositions.() -> PlaybackPositions) =
        updateState { copy(playbackPositions = func(playbackPositions)) }

    private fun updatePlaybackProperties(func: PlaybackProperties.() -> PlaybackProperties) =
        updateState { copy(playbackProperties = func(playbackProperties)) }

    private fun updateFileSaveDialogProperties(func: FileSaveDialogProperties.() -> FileSaveDialogProperties) =
        updateState { copy(fileSaveDialogProperties = func(fileSaveDialogProperties)) }

    override fun onUiIntent(intent: TrimmerUiIntent) = when (intent) {
        is TrimmerUiIntent.LoadTrack -> {
            loadTrack(trackPath = intent.trackPath)
            loadTrackAmplitudes(trackPath = intent.trackPath)
        }

        is TrimmerUiIntent.ShowEffect -> updateState {
            copy(shownEffects = intent.shownEffects)
        }

        is TrimmerUiIntent.UpdateFocusEvent -> updateState {
            copy(focusEvent = intent.focusEvent)
        }

        is TrimmerUiIntent.TrimTrack -> sendTrimRequest()

        is TrimmerUiIntent.Lifecycle -> onLifecycleUiIntent(intent = intent)

        is TrimmerUiIntent.Waveform -> onWaveformUiIntent(intent = intent)

        is TrimmerUiIntent.Player -> onPlayerUiIntent(intent = intent)

        is TrimmerUiIntent.Positions -> onPositionUiIntent(intent = intent)

        is TrimmerUiIntent.FileSave -> onFileSaveUiIntent(intent = intent)
    }

    private fun onLifecycleUiIntent(intent: TrimmerUiIntent.Lifecycle) = when (intent) {
        is TrimmerUiIntent.Lifecycle.OnStart -> {
            subscribeOnDataUpdates()
            launchPlayerTasks()
        }

        is TrimmerUiIntent.Lifecycle.OnStop -> cleanUp()
    }

    private fun onWaveformUiIntent(intent: TrimmerUiIntent.Waveform) = when (intent) {
        is TrimmerUiIntent.Waveform.ZoomIn -> zoomIn()
        is TrimmerUiIntent.Waveform.ZoomOut -> zoomOut()
        is TrimmerUiIntent.Waveform.UpdateZoomLevel -> updateZoomLevel(zoom = intent.zoom)
        is TrimmerUiIntent.Waveform.UpdateZoomSteps -> updateZoomSteps(zoomSteps = intent.zoomSteps)
    }

    private fun onPlayerUiIntent(intent: TrimmerUiIntent.Player) = when (intent) {
        is TrimmerUiIntent.Player.SeekTenSecsBack -> viewModelScope.sideEffect(Dispatchers.Default) {
            withPlayer { seekTenSecsBack(startPosition = state.playbackPositions.startPosInMillis) }
        }

        is TrimmerUiIntent.Player.SeekTenSecsForward -> viewModelScope.sideEffect(Dispatchers.Default) {
            withPlayer { seekTenSecsForward(totalDuration = state.trackDurationInMillis) }
        }

        is TrimmerUiIntent.Player.UpdatePlayingState -> updatePlaybackProperties {
            copy(isPlaying = isPlaying.not())
        }

        is TrimmerUiIntent.Player.UpdatePitch -> updatePlaybackProperties {
            copy(pitch = intent.pitch)
        }

        is TrimmerUiIntent.Player.UpdateSpeed -> updatePlaybackProperties {
            copy(speed = intent.speed)
        }
    }

    private fun onPositionUiIntent(intent: TrimmerUiIntent.Positions) = when (intent) {
        is TrimmerUiIntent.Positions.UpdateEndPosition -> updatePlaybackPositions {
            copy(endPosInMillis = intent.endPositionInMillis)
        }

        is TrimmerUiIntent.Positions.UpdateStartPosition -> updatePlaybackPositions {
            copy(startPosInMillis = intent.startPositionInMillis)
        }

        is TrimmerUiIntent.Positions.SeekTo -> seekTo(playbackPosition = intent.playbackPosition)

        is TrimmerUiIntent.Positions.UpdateFadeIn -> updatePlaybackPositions {
            copy(fadeInSecs = intent.position)
        }

        is TrimmerUiIntent.Positions.UpdateFadeOut -> updatePlaybackPositions {
            copy(fadeOutSecs = intent.position)
        }
    }

    private fun onFileSaveUiIntent(intent: TrimmerUiIntent.FileSave) = when (intent) {
        is TrimmerUiIntent.FileSave.SelectSaveOption -> updateFileSaveDialogProperties {
            copy(selectedSaveOptionIndex = intent.saveOptionIndex)
        }

        is TrimmerUiIntent.FileSave.UpdateDialogVisibility -> updateFileSaveDialogProperties {
            copy(isDialogVisible = intent.isVisible)
        }

        is TrimmerUiIntent.FileSave.UpdateFilename -> updateFilename(filename = intent.filename)
    }

    private fun sendTrimRequest() = viewModelScope.sideEffect(Dispatchers.IO) {
        state.trackState.getOrNull()?.let { track ->
            WorkManager
                .getInstance(appContext)
                .enqueue(
                    OneTimeWorkRequestBuilder<TrimmerWorker>()
                        .setInputData(
                            workDataOf(
                                TrimmerWorker.REQUEST_KEY to json.encodeToString(
                                    TrimmerWorkRequest(
                                        track = DefaultTrack(track),
                                        outputFilename = state.fileSaveDialogProperties.filename,
                                        audioFormat = state.fileSaveDialogProperties.audioFormat,
                                        trimRange = state.playbackPositions.trimRange,
                                        pitchAndSpeed = state.playbackProperties.pitchAndSpeed,
                                        fadeDurations = state.playbackPositions.fadeDurations,
                                    )
                                )
                            )
                        )
                        .build()
                )
        }
    }

    private fun updateFilename(filename: String) = updateFileSaveDialogProperties {
        copy(filename = filename)
    }

    private fun loadTrack(trackPath: String) = viewModelScope.sideEffect(Dispatchers.Default) {
        updateState { copy(trackState = UiState.Loading) }
        updateFilename(filename = initialFilename(trackPath = trackPath))

        val trackUiState = tracksRepository
            .getTrackFromMediaStore(trackPath = trackPath)
            ?.let { TrackUiState.fromDTO(it).toUiState() }
            ?: UiState.Error()

        trackUiState.getOrNull()?.let {
            prepareTrackPlayerCatching(track = it)
        }

        updateState {
            copy(
                trackState = trackUiState,
                playbackPositions = PlaybackPositions(
                    startPosInMillis = 0L,
                    endPosInMillis = trackUiState.fold(
                        ifPresent = TrackUiState::durationMillis,
                        ifEmpty = { 0L },
                    )
                ),
            )
        }
    }

    private fun initialFilename(trackPath: String) =
        trackPath.let(::File).nameWithoutExtension

    private fun loadTrackAmplitudes(trackPath: String) =
        viewModelScope.sideEffect(Dispatchers.Default) {
            waveformRepository.updateAmplitudes(
                amplituda
                    .processAudio(trackPath)
                    .get(AmplitudaErrorListener { it.printStackTrace() })
                    .amplitudesAsList()
            )
        }

    private suspend fun prepareTrackPlayerCatching(track: Track) = Either.catch {
        withPlayer {
            setMediaItem(track.toMediaItem())
            prepare()
        }
    }

    private fun subscribeOnDataUpdates() {
        dataUpdatesJob = viewModelScope.launch(Dispatchers.Default) {
            waveformRepository
                .amplitudesFlow
                .map { it.toImmutableList() }
                .collectLatest {
                    updateState { copy(amplitudes = it) }
                }
        }
    }

    private fun unsubscribeFromDataUpdates() {
        dataUpdatesJob?.cancel()
        dataUpdatesJob = null
    }

    private fun zoomIn() = updateWaveformProperties {
        copy(
            zoomLevel = when (zoomLevel) {
                zoomSteps -> zoomLevel
                else -> zoomLevel + 1
            }
        )
    }

    private fun zoomOut() = updateWaveformProperties {
        copy(
            zoomLevel = when (zoomLevel) {
                InitialZoomLevel -> InitialZoomLevel
                else -> zoomLevel - 1
            }
        )
    }

    private fun updateZoomLevel(zoom: Int) = updateWaveformProperties {
        copy(zoomLevel = zoom)
    }

    private fun updateZoomSteps(zoomSteps: Int) = updateWaveformProperties {
        copy(zoomSteps = zoomSteps)
    }

    private fun Player.onPlaybackLaunched() = viewModelScope.sideEffect(Dispatchers.Main) {
        while (isPlaying) {
            updatePlaybackPositions { copy(playbackPosInMillis = currentPosition) }
            delay(PlaybackUpdateCooldown)
        }

        notifyPlaybackTaskFinished()
    }

    private suspend fun notifyPlaybackTaskFinished() {
        updatePlaybackProperties { copy(isPlaybackTaskFinished = true) }
        resetPlaybackPosCondVar.notify()
    }

    private fun onCompletion() = viewModelScope.sideEffect(Dispatchers.Default) {
        updatePlaybackProperties { copy(isPlaying = false) }
        resetPlaybackPosition()
    }

    private suspend fun resetPlaybackPosition() {
        while (state.playbackProperties.isPlaybackTaskFinished.not())
            resetPlaybackPosCondVar.wait()

        setPlaybackPositionToStart()
        updatePlaybackProperties { copy(isPlaybackTaskFinished = false) }
    }

    private fun setPlaybackPositionToStart() =
        updatePlaybackPositions { copy(playbackPosInMillis = startPosInMillis) }

    private fun launchPlaybackPositionOutOfBordersMonitoring() {
        playbackPositionOutOfBordersJob = viewModelScope.launch(Dispatchers.Default) {
            stateFlow
                .map { state ->
                    Tuple4(
                        state.playbackProperties.isPlayerInitialized,
                        state.playbackPositions.playbackPosInMillis,
                        state.playbackPositions.startPosInMillis,
                        state.playbackPositions.endPosInMillis,
                    )
                }
                .distinctUntilChanged()
                .collectLatest { (isPlayerInitialized, playbackPos, startPos, endPos) ->
                    val isPlaybackPositionNotInBounds = playbackPos !in startPos..endPos

                    if (isPlayerInitialized && isPlaybackPositionNotInBounds)
                        onCompletion()
                }
        }
    }

    private fun releasePlaybackPositionOutOfBordersMonitoring() {
        playbackPositionOutOfBordersJob?.cancel()
        playbackPositionOutOfBordersJob = null
    }

    private fun launchPlayPauseMonitoring() {
        playPauseJob = viewModelScope.launch(Dispatchers.Default) {
            stateFlow
                .map { state ->
                    Triple(
                        state.playbackProperties.isPlaying,
                        state.playbackPositions.startPosInMillis,
                        state.playbackProperties.isPlayerInitialized,
                    )
                }
                .distinctUntilChanged()
                .collectLatest { (isPlaying, startPos, isPlayerInitialized) ->
                    when {
                        isPlaying -> {
                            updatePlaybackProperties { copy(isPlayerInitialized = true) }
                            withPlayer {
                                seekTo(startPos)
                                playWhenReady = true
                            }
                        }

                        isPlayerInitialized -> {
                            withPlayer(Player::pause)
                            resetPlaybackPosition()
                        }
                    }
                }
        }
    }

    private fun releasePlayPauseMonitoring() {
        playPauseJob?.cancel()
        playPauseJob = null
    }

    private fun launchAudioEffectsMonitoring() {
        audioEffectsJob = viewModelScope.launch(Dispatchers.Default) {
            stateFlow
                .map { state -> state.playbackProperties.run { speed to pitch } }
                .distinctUntilChanged()
                .collectLatest { (speed, pitch) ->
                    withPlayer { playbackParameters = PlaybackParameters(speed, pitch) }
                }
        }
    }

    private fun seekTo(playbackPosition: Long) = viewModelScope.sideEffect(Dispatchers.Default) {
        withPlayer { Either.catch { trackPlayer.seekTo(playbackPosition) } }
    }

    private fun launchPlayerTasks() {
        launchPlaybackPositionOutOfBordersMonitoring()
        launchPlayPauseMonitoring()
        launchAudioEffectsMonitoring()
    }

    private fun releasePlayerTasks() {
        releasePlaybackPositionOutOfBordersMonitoring()
        releasePlayPauseMonitoring()
    }

    private fun releasePlayer() = viewModelScope.sideEffect {
        withPlayer(Player::stopAndReleaseCatching)
        resetPlaybackStates()
    }

    private fun resetPlaybackStates() {
        resetPlayerStates()
        resetAudioEffects()
    }

    private fun resetPlayerStates() {
        updatePlaybackProperties {
            copy(
                isPlayerInitialized = false,
                isPlaying = false,
                isPlaybackTaskFinished = true,
            )
        }

        setPlaybackPositionToStart()
    }

    private fun resetAudioEffects() {
        updatePlaybackPositions {
            copy(fadeInSecs = InitialPosition, fadeOutSecs = InitialPosition)
        }

        updatePlaybackProperties {
            copy(pitch = InitialPitch, speed = InitialSpeed)
        }
    }

    private fun cleanUp() {
        unsubscribeFromDataUpdates()
        releasePlayerTasks()
        releasePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        clearAmplitudes()
        cleanUp()
    }

    private fun clearAmplitudes() = viewModelScope.sideEffect(Dispatchers.IO) {
        waveformRepository.updateAmplitudes(emptyList())
    }
}
