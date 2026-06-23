package com.paranid5.crescendo.feature.playing.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.image.ImageRetriever
import com.paranid5.crescendo.domain.image.model.BitmapDrawableWithPalette
import com.paranid5.crescendo.domain.image.model.ImagePath
import com.paranid5.crescendo.domain.image.model.ImageUrl
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.feature.playing.domain.PlayingInteractor
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.doNothing
import com.paranid5.crescendo.utils.extensions.launchInScope
import com.paranid5.feature.metadata.VideoMetadataUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal class PlayingViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val playbackRepository: PlaybackRepository,
    private val tracksRepository: TracksRepository,
    private val streamRepository: StreamRepository,
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val interactor: PlayingInteractor,
    private val trackServiceInteractor: TrackServiceInteractor,
    private val imageRetriever: ImageRetriever,
) : ViewModel(), PlayingViewModel, StatePublisher<PlayingState> {
    companion object {
        private const val STATE_KEY = "state"
    }

    private var subscribeDataUpdatesJob: Job? = null

    override val stateFlow = savedStateHandle.getStateFlow(STATE_KEY, PlayingState())

    override fun updateState(func: PlayingState.() -> PlayingState) {
        savedStateHandle[STATE_KEY] = func(state)
    }

    private val _effectFlow = MutableSharedFlow<PlayingScreenEffect>()

    override val effectFlow = _effectFlow.asSharedFlow()

    override suspend fun retrieveBitmapDrawableFromMediaWithPalette(
        path: ImagePath,
    ): BitmapDrawableWithPalette? = imageRetriever
        .retrieveBitmapDrawableFromMediaWithPalette(path = path)

    override suspend fun downloadBitmapDrawableWithPalette(
        url: ImageUrl,
    ): BitmapDrawableWithPalette? = imageRetriever
        .downloadBitmapDrawableWithPalette(url = url)

    override fun onUiIntent(intent: PlayingUiIntent) {
        when (intent) {
            is PlayingUiIntent.UpdateState -> onUpdateStateUiIntent(intent)
            is PlayingUiIntent.Lifecycle -> onLifecycleUiIntent(intent)
            is PlayingUiIntent.Playback -> onPlaybackUiIntent(intent)
            is PlayingUiIntent.ScreenEffect -> onScreenEventUiIntent(intent)
        }
    }

    private fun onUpdateStateUiIntent(intent: PlayingUiIntent.UpdateState) = when (intent) {
        is PlayingUiIntent.UpdateState.UpdateUiParams -> updateState {
            copy(visiblePlaybackStatus = intent.visiblePlaybackStatus)
        }

        is PlayingUiIntent.UpdateState.LikeClick -> updateState {
            copy(isLiked = isLiked.not())
        }

        is PlayingUiIntent.UpdateState.AddTrackToPlaylist -> addToPlaylist(track = intent.track)
    }

    private fun onLifecycleUiIntent(intent: PlayingUiIntent.Lifecycle) = when (intent) {
        is PlayingUiIntent.Lifecycle.OnStart -> subscribeOnDataUpdates()
        is PlayingUiIntent.Lifecycle.OnStop -> unsubscribeFromDataUpdates()
    }

    private fun onPlaybackUiIntent(intent: PlayingUiIntent.Playback) = when (intent) {
        is PlayingUiIntent.Playback.NextButtonClick -> onNextButtonClick()
        is PlayingUiIntent.Playback.PauseButtonClick -> onPauseButtonClick()
        is PlayingUiIntent.Playback.PlayButtonClick -> onPlayButtonClick()
        is PlayingUiIntent.Playback.PrevButtonClick -> onPrevButtonClick()
        is PlayingUiIntent.Playback.RepeatClick -> onRepeatClick()
        is PlayingUiIntent.Playback.SeekTo -> onSeekTo(position = intent.position)
        is PlayingUiIntent.Playback.SeekToLiveStreamRealPosition ->
            interactor.sendSeekToLiveStreamRealPosition()
    }

    private fun onScreenEventUiIntent(intent: PlayingUiIntent.ScreenEffect) = when (intent) {
        is PlayingUiIntent.ScreenEffect.ShowAudioEffects ->
            onAudioEffectsClick()

        is PlayingUiIntent.ScreenEffect.ShowTrimmer ->
            onShowTrimmerClick(trackUri = intent.trackUri)

        is PlayingUiIntent.ScreenEffect.ShowMetaEditor ->
            onShowMetaEditorClick(trackUri = intent.trackUri)
    }

    private fun onSeekTo(position: Long) = nullable {
        val audioStatus = state.visiblePlaybackStatus.bind()

        viewModelScope.launchInScope {
            interactor.updateSeekToPosition(playbackStatus = audioStatus, position = position)
        }

        interactor.sendSeekToBroadcast(playbackStatus = audioStatus, position = position)
    }

    private fun onPrevButtonClick() = nullable {
        val audioStatus = state.visiblePlaybackStatus.bind()

        viewModelScope.launchInScope {
            playbackRepository.updateAudioStatus(playbackStatus = audioStatus)

            audioStatus.fold(
                ifStream = doNothing,
                ifTrack = { resetTrackPlaybackPosition() },
            )
        }

        interactor.sendOnPrevButtonClickedBroadcast(playbackStatus = audioStatus)
    }

    private fun onPauseButtonClick() = nullable {
        val audioStatus = state.visiblePlaybackStatus.bind()

        viewModelScope.launchInScope {
            playbackRepository.updateAudioStatus(playbackStatus = audioStatus)
        }

        interactor.sendPauseBroadcast(playbackStatus = audioStatus)
    }

    private fun onPlayButtonClick() = nullable {
        val audioStatus = state.visiblePlaybackStatus.bind()

        viewModelScope.launchInScope {
            playbackRepository.updateAudioStatus(playbackStatus = audioStatus)
        }

        interactor.startStreamingOrSendResumeBroadcast(playbackStatus = audioStatus)
    }

    private fun onNextButtonClick() = nullable {
        val audioStatus = state.visiblePlaybackStatus.bind()

        viewModelScope.launchInScope {
            playbackRepository.updateAudioStatus(playbackStatus = audioStatus)

            audioStatus.fold(
                ifStream = doNothing,
                ifTrack = { resetTrackPlaybackPosition() },
            )
        }

        interactor.sendOnNextButtonClickedBroadcast(playbackStatus = audioStatus)
    }

    private suspend inline fun resetTrackPlaybackPosition() =
        playbackRepository.updateTracksPlaybackPosition(0)

    private fun onAudioEffectsClick() = when {
        interactor.isAllowedToShowAudioEffects -> viewModelScope.launchInScope(Dispatchers.Main) {
            _effectFlow.emit(PlayingScreenEffect.ShowAudioEffects)
        }

        else -> viewModelScope.launchInScope(Dispatchers.Main) {
            _effectFlow.emit(PlayingScreenEffect.ShowAudioEffectsNotAllowed)
        }
    }

    private fun onShowTrimmerClick(trackUri: String) {
        viewModelScope.launchInScope(Dispatchers.Main) {
            _effectFlow.emit(PlayingScreenEffect.ShowTrimmer(trackUri = trackUri))
        }
    }

    private fun onShowMetaEditorClick(trackUri: String) {
        viewModelScope.launchInScope(Dispatchers.Main) {
            _effectFlow.emit(PlayingScreenEffect.ShowMetaEditor(trackUri = trackUri))
        }
    }

    private fun onRepeatClick() = nullable {
        val audioStatus = state.visiblePlaybackStatus.bind()
        interactor.sendChangeRepeatBroadcast(playbackStatus = audioStatus)
    }

    private fun addToPlaylist(track: Track) {
        val defaultTrack = DefaultTrack(track)
        trackServiceInteractor.addToPlaylist(defaultTrack)
        viewModelScope.launchInScope { currentPlaylistRepository.addTrackToPlaylist(defaultTrack) }
    }

    private fun subscribeOnDataUpdates() {
        subscribeDataUpdatesJob = viewModelScope.launchInScope(Dispatchers.Default) {
            combine(
                playbackRepository.audioSessionIdState,
                playbackRepository.isPlayingState,
                playbackRepository.playbackStatusFlow,
                playbackRepository.streamPlaybackPositionFlow,
                playbackRepository.tracksPlaybackPositionFlow,
                playbackRepository.isRepeatingFlow,
                tracksRepository.currentTrackFlow,
                streamRepository.currentMetadataFlow,
                streamRepository.playingUrlFlow,
            ) { params ->
                PlayingState(
                    audioSessionId = params[0] as Int,
                    isPlaying = params[1] as Boolean,
                    actualPlaybackStatus = params[2] as PlaybackStatus?,
                    streamPlaybackPosition = params[3] as Long,
                    trackPlaybackPosition = params[4] as Long,
                    isRepeating = params[5] as Boolean,
                    currentTrack = (params[6] as Track?)
                        ?.let(TrackUiState.Companion::fromDTO),
                    currentMetadata = (params[7] as VideoMetadata?)
                        ?.let(VideoMetadataUiState.Companion::fromDTO),
                    playingStreamUrl = params[8] as String,
                )
            }.distinctUntilChanged().collectLatest { mediator ->
                updateState {
                    copy(
                        audioSessionId = mediator.audioSessionId,
                        isPlaying = mediator.isPlaying,
                        actualPlaybackStatus = mediator.actualPlaybackStatus,
                        streamPlaybackPosition = mediator.streamPlaybackPosition,
                        trackPlaybackPosition = mediator.trackPlaybackPosition,
                        isRepeating = mediator.isRepeating,
                        currentTrack = mediator.currentTrack,
                        currentMetadata = mediator.currentMetadata,
                        playingStreamUrl = mediator.playingStreamUrl,
                    )
                }
            }
        }
    }

    private fun unsubscribeFromDataUpdates() {
        subscribeDataUpdatesJob?.cancel()
        subscribeDataUpdatesJob = null
    }
}
