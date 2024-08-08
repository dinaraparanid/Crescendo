package com.paranid5.crescendo.feature.playing.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.feature.playing.domain.PlayingInteractor
import com.paranid5.crescendo.ui.metadata.VideoMetadataUiState
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

internal class PlayingViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val playbackRepository: PlaybackRepository,
    private val tracksRepository: TracksRepository,
    private val streamRepository: StreamRepository,
    private val interactor: PlayingInteractor,
) : ViewModel(), PlayingViewModel, StatePublisher<PlayingState> {
    companion object {
        private const val StateKey = "state"
    }

    private var subscribeDataUpdatesJob: Job? = null

    override val stateFlow = savedStateHandle.getStateFlow(StateKey, PlayingState())

    override fun updateState(func: PlayingState.() -> PlayingState) {
        savedStateHandle[StateKey] = func(state)
    }

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
            copy(
                screenAudioStatus = intent.screenAudioStatus,
                coverAlpha = intent.coverAlpha,
            )
        }

        is PlayingUiIntent.UpdateState.LikeClick -> updateState {
            copy(isLiked = isLiked.not())
        }
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
        is PlayingUiIntent.ScreenEffect.ClearScreenEffect -> updateState {
            copy(screenEffect = null)
        }

        is PlayingUiIntent.ScreenEffect.ShowAudioEffects -> onAudioEffectsClick()

        is PlayingUiIntent.ScreenEffect.ShowTrimmer -> updateState {
            copy(screenEffect = PlayingScreenEffect.ShowTrimmer(trackUri = intent.trackUri))
        }
    }

    private fun onSeekTo(position: Long) = nullable {
        val audioStatus = state.screenAudioStatus.bind()

        viewModelScope.launch {
            interactor.updateSeekToPosition(audioStatus = audioStatus, position = position)
        }

        interactor.sendSeekToBroadcast(audioStatus = audioStatus, position = position)
    }

    private fun onPrevButtonClick() = nullable {
        val audioStatus = state.screenAudioStatus.bind()
        viewModelScope.launch { playbackRepository.updateAudioStatus(audioStatus = audioStatus) }
        interactor.sendOnPrevButtonClickedBroadcast(audioStatus = audioStatus)
    }

    private fun onPauseButtonClick() = nullable {
        val audioStatus = state.screenAudioStatus.bind()
        viewModelScope.launch { playbackRepository.updateAudioStatus(audioStatus = audioStatus) }
        interactor.sendPauseBroadcast(audioStatus = audioStatus)
    }

    private fun onPlayButtonClick() = nullable {
        val audioStatus = state.screenAudioStatus.bind()
        viewModelScope.launch { playbackRepository.updateAudioStatus(audioStatus = audioStatus) }
        interactor.startStreamingOrSendResumeBroadcast(audioStatus = audioStatus)
    }

    private fun onNextButtonClick() = nullable {
        val audioStatus = state.screenAudioStatus.bind()
        viewModelScope.launch { playbackRepository.updateAudioStatus(audioStatus = audioStatus) }
        interactor.sendOnNextButtonClickedBroadcast(audioStatus = audioStatus)
    }

    private fun onAudioEffectsClick() = when {
        interactor.isAllowedToShowAudioEffects -> updateState {
            copy(screenEffect = PlayingScreenEffect.ShowAudioEffects)
        }

        else -> updateState {
            copy(screenEffect = PlayingScreenEffect.ShowAudioEffectsNotAllowed)
        }
    }

    private fun onRepeatClick() = nullable {
        val audioStatus = state.screenAudioStatus.bind()
        interactor.sendChangeRepeatBroadcast(audioStatus = audioStatus)
    }

    private fun subscribeOnDataUpdates() {
        subscribeDataUpdatesJob = viewModelScope.launch(Dispatchers.Default) {
            combine(
                playbackRepository.audioSessionIdState,
                playbackRepository.isPlayingState,
                playbackRepository.audioStatusFlow,
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
                    actualAudioStatus = params[2] as AudioStatus?,
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
                        actualAudioStatus = mediator.actualAudioStatus,
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
