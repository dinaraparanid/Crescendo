package com.paranid5.crescendo.feature.playing.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.feature.playing.domain.PlayingInteractor
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
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
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val interactor: PlayingInteractor,
    private val trackServiceInteractor: TrackServiceInteractor,
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
                screenPlaybackStatus = intent.screenPlaybackStatus,
                coverAlpha = intent.coverAlpha,
            )
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
        is PlayingUiIntent.ScreenEffect.ClearScreenEffect -> updateState {
            copy(screenEffect = null)
        }

        is PlayingUiIntent.ScreenEffect.ShowAudioEffects -> onAudioEffectsClick()

        is PlayingUiIntent.ScreenEffect.ShowTrimmer -> updateState {
            copy(screenEffect = PlayingScreenEffect.ShowTrimmer(trackUri = intent.trackUri))
        }

        PlayingUiIntent.ScreenEffect.ShowMetaEditor -> updateState {
            copy(screenEffect = PlayingScreenEffect.ShowMetaEditor)
        }
    }

    private fun onSeekTo(position: Long) = nullable {
        val audioStatus = state.screenPlaybackStatus.bind()

        viewModelScope.launch {
            interactor.updateSeekToPosition(playbackStatus = audioStatus, position = position)
        }

        interactor.sendSeekToBroadcast(playbackStatus = audioStatus, position = position)
    }

    private fun onPrevButtonClick() = nullable {
        val audioStatus = state.screenPlaybackStatus.bind()
        viewModelScope.launch { playbackRepository.updateAudioStatus(playbackStatus = audioStatus) }
        interactor.sendOnPrevButtonClickedBroadcast(playbackStatus = audioStatus)
    }

    private fun onPauseButtonClick() = nullable {
        val audioStatus = state.screenPlaybackStatus.bind()
        viewModelScope.launch { playbackRepository.updateAudioStatus(playbackStatus = audioStatus) }
        interactor.sendPauseBroadcast(playbackStatus = audioStatus)
    }

    private fun onPlayButtonClick() = nullable {
        val audioStatus = state.screenPlaybackStatus.bind()
        viewModelScope.launch { playbackRepository.updateAudioStatus(playbackStatus = audioStatus) }
        interactor.startStreamingOrSendResumeBroadcast(playbackStatus = audioStatus)
    }

    private fun onNextButtonClick() = nullable {
        val audioStatus = state.screenPlaybackStatus.bind()
        viewModelScope.launch { playbackRepository.updateAudioStatus(playbackStatus = audioStatus) }
        interactor.sendOnNextButtonClickedBroadcast(playbackStatus = audioStatus)
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
        val audioStatus = state.screenPlaybackStatus.bind()
        interactor.sendChangeRepeatBroadcast(playbackStatus = audioStatus)
    }

    private fun addToPlaylist(track: Track) {
        val defaultTrack = DefaultTrack(track)
        trackServiceInteractor.addToPlaylist(defaultTrack)
        viewModelScope.launch { currentPlaylistRepository.addTrackToPlaylist(defaultTrack) }
    }

    private fun subscribeOnDataUpdates() {
        subscribeDataUpdatesJob = viewModelScope.launch(Dispatchers.Default) {
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
