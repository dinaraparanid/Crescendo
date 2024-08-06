package com.paranid5.crescendo.tracks.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.AudioStatus
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
        is TracksUiIntent.OnStart -> onStart()

        is TracksUiIntent.OnStop -> unsubscribeFromDataUpdates()

        is TracksUiIntent.OnRefresh -> onRefresh()

        is TracksUiIntent.UpdateSearchQuery -> updateState {
            copy(query = intent.query)
        }

        is TracksUiIntent.UpdateTrackOrder -> viewModelScope.sideEffect {
            tracksRepository.updateTrackOrder(intent.order)
            fetchTracksFromMediaStore()
        }

        is TracksUiIntent.TrackClick -> onTrackClick(
            nextPlaylist = intent.nextPlaylist,
            nextTrackIndex = intent.nextTrackIndex,
        )

        is TracksUiIntent.ShowTrimmer -> updateState {
            copy(backResult = TracksBackResult.ShowTrimmer(intent.trackUri))
        }

        is TracksUiIntent.ClearBackResult -> updateState {
            copy(backResult = null)
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
            playbackRepository.updateAudioStatus(audioStatus = AudioStatus.PLAYING)
            currentPlaylistRepository.updateCurrentPlaylist(playlist = nextPlaylist)
            tracksRepository.updateCurrentTrackIndex(index = nextTrackIndex)

            trackServiceInteractor.startPlaylistPlayback(
                nextTrack = nextPlaylist.getOrNull(nextTrackIndex),
                prevTrack = currentTrack,
            )
        }
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
