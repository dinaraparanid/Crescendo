package com.paranid5.crescendo.tracks.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.tracks.sortedBy
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
import com.paranid5.crescendo.system.services.track.startPlaylistPlayback
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.toUiState
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.extensions.mapToImmutableList
import com.paranid5.crescendo.utils.extensions.sideEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

internal class TracksViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val playbackRepository: PlaybackRepository,
    private val tracksRepository: TracksRepository,
    private val trackServiceInteractor: TrackServiceInteractor,
) : ViewModel(), TracksViewModel, StatePublisher<TracksState> {
    companion object {
        private const val StateKey = "state"
    }

    private var subscribeDataJob: Job? = null

    override val stateFlow = savedStateHandle.getStateFlow(StateKey, TracksState())

    override fun updateState(func: TracksState.() -> TracksState) {
        savedStateHandle[StateKey] = func(state)
    }

    override fun onUiIntent(intent: TracksUiIntent) = when (intent) {
        is TracksUiIntent.Lifecycle -> onLifecycleUiIntent(intent)
        is TracksUiIntent.Tracks -> onTracksUiIntent(intent)
        is TracksUiIntent.UpdateState -> onUpdateStateUiIntent(intent)
        is TracksUiIntent.ScreenEffect -> onScreenEffectUiIntent(intent)
    }

    private fun onLifecycleUiIntent(intent: TracksUiIntent.Lifecycle) = when (intent) {
        is TracksUiIntent.Lifecycle.OnRefresh -> onRefresh()
        is TracksUiIntent.Lifecycle.OnStart -> onStart()
        is TracksUiIntent.Lifecycle.OnStop -> unsubscribeFromDataUpdates()
    }

    private fun onTracksUiIntent(intent: TracksUiIntent.Tracks) = when (intent) {
        is TracksUiIntent.Tracks.AddTrackToPlaylist -> addToPlaylist(track = intent.track)
        is TracksUiIntent.Tracks.TrackClick -> onTrackClick(
            nextPlaylist = intent.nextPlaylist,
            nextTrackIndex = intent.nextTrackIndex,
        )
    }

    private fun onUpdateStateUiIntent(intent: TracksUiIntent.UpdateState) = when (intent) {
        is TracksUiIntent.UpdateState.UpdateSearchQuery -> updateState {
            copy(query = intent.query)
        }

        is TracksUiIntent.UpdateState.UpdateTrackOrder -> viewModelScope.sideEffect {
            tracksRepository.updateTrackOrder(intent.order)
            fetchTracksFromMediaStore()
        }
    }

    private fun onScreenEffectUiIntent(intent: TracksUiIntent.ScreenEffect) = when (intent) {
        is TracksUiIntent.ScreenEffect.ClearBackResult -> updateState {
            copy(screenEffect = null)
        }

        is TracksUiIntent.ScreenEffect.ShowMetaEditor -> updateState {
            copy(screenEffect = TracksScreenEffect.ShowMetaEditor)
        }

        is TracksUiIntent.ScreenEffect.ShowTrimmer -> updateState {
            copy(screenEffect = TracksScreenEffect.ShowTrimmer(intent.trackUri))
        }
    }

    private fun onStart() {
        updateState { copy(allTracksState = UiState.Loading) }
        subscribeOnDataUpdates()
        fetchTracksFromMediaStore()
    }

    private fun onRefresh() {
        updateState { copy(allTracksState = UiState.Refreshing(allTracksState)) }
        fetchTracksFromMediaStore()
    }

    private fun onTrackClick(
        nextPlaylist: List<Track>,
        nextTrackIndex: Int,
    ) {
        val currentTrack = state.currentTrack

        viewModelScope.launch {
            playbackRepository.updateAudioStatus(playbackStatus = PlaybackStatus.PLAYING)
            currentPlaylistRepository.updateCurrentPlaylist(playlist = nextPlaylist)
            tracksRepository.updateCurrentTrackIndex(index = nextTrackIndex)

            trackServiceInteractor.startPlaylistPlayback(
                nextTrack = nextPlaylist.getOrNull(nextTrackIndex),
                prevTrack = currentTrack,
            )
        }
    }

    private fun addToPlaylist(track: Track) {
        val defaultTrack = DefaultTrack(track)
        trackServiceInteractor.addToPlaylist(defaultTrack)
        viewModelScope.launch { currentPlaylistRepository.addTrackToPlaylist(defaultTrack) }
    }

    private fun subscribeOnDataUpdates() {
        subscribeDataJob = viewModelScope.launch(Dispatchers.Default) {
            combine(
                tracksRepository.trackOrderFlow,
                tracksRepository.currentTrackFlow,
            ) { trackOrder, currentTrack ->
                trackOrder to currentTrack
            }.distinctUntilChanged().collectLatest { (trackOrder, currentTrack) ->
                updateState {
                    copy(
                        trackOrder = trackOrder,
                        currentTrack = currentTrack?.let(TrackUiState.Companion::fromDTO),
                    )
                }
            }
        }
    }

    private fun unsubscribeFromDataUpdates() {
        subscribeDataJob?.cancel()
        subscribeDataJob = null
    }

    private fun fetchTracksFromMediaStore() = viewModelScope.sideEffect(Dispatchers.Default) {
        val tracksState = tracksRepository
            .getAllTracksFromMediaStore()
            .sortedBy(state.trackOrder)
            .mapToImmutableList(TrackUiState.Companion::fromDTO)
            .toUiState()

        updateState { copy(allTracksState = tracksState) }
    }
}
