package com.paranid5.crescendo.current_playlist.presentation.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.current_playlist.presentation.view_model.CurrentPlaylistState.DismissState
import com.paranid5.crescendo.current_playlist.presentation.view_model.CurrentPlaylistState.PlaylistState
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.current_playlist.currentPlaylistDurationFormattedFlow
import com.paranid5.crescendo.domain.current_playlist.currentPlaylistSizeFlow
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
import com.paranid5.crescendo.system.services.track.startPlaylistPlayback
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.extensions.exclude
import com.paranid5.crescendo.utils.extensions.mapToImmutableList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class CurrentPlaylistViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val playbackRepository: PlaybackRepository,
    private val tracksRepository: TracksRepository,
    private val trackServiceInteractor: TrackServiceInteractor
) : ViewModel(),
    CurrentPlaylistViewModel,
    StatePublisher<CurrentPlaylistState> {
    companion object {
        private const val StateKey = "state"
        private const val UpdatePlaylistAfterDragDelay = 500L
    }

    private var playlistUpdatesJob: Job? = null
    private var dismissUpdatesJob: Job? = null

    override val stateFlow =
        savedStateHandle.getStateFlow(StateKey, CurrentPlaylistState())

    override fun updateState(func: CurrentPlaylistState.() -> CurrentPlaylistState) {
        savedStateHandle[StateKey] = func(state)
    }

    private inline fun updateDismissState(crossinline func: DismissState.() -> DismissState) =
        updateState { copy(dismissState = func(dismissState)) }

    private inline fun updatePlaylistState(crossinline func: PlaylistState.() -> PlaylistState) =
        updateState { copy(playlistState = func(playlistState)) }

    override fun onUiIntent(intent: CurrentPlaylistUiIntent) {
        when (intent) {
            is CurrentPlaylistUiIntent.OnStart -> {
                subscribeOnPlaylistUpdates()
                subscribeOnDismissUpdates()
            }

            is CurrentPlaylistUiIntent.OnStop -> {
                unsubscribeFromPlaylistUpdates()
                unsubscribeFromDismissUpdates()
            }

            is CurrentPlaylistUiIntent.DismissTrack ->
                dismissTrack(index = intent.index)

            is CurrentPlaylistUiIntent.UpdateAfterDrag -> viewModelScope.launch {
                updateCurrentPlaylistAfterDrag(
                    newPlaylist = intent.newPlaylist,
                    newCurrentTrackIndex = intent.newCurrentTrackIndex,
                )
            }

            is CurrentPlaylistUiIntent.StartPlaylistPlayback -> viewModelScope.launch {
                startPlaylistPlayback(trackIndex = intent.trackIndex)
            }
        }
    }

    private fun subscribeOnPlaylistUpdates() {
        playlistUpdatesJob = viewModelScope.launch(Dispatchers.Default) {
            combine(
                currentPlaylistRepository.currentPlaylistFlow,
                tracksRepository.currentTrackFlow,
                tracksRepository.currentTrackIndexFlow,
                currentPlaylistRepository.currentPlaylistSizeFlow,
                currentPlaylistRepository.currentPlaylistDurationFormattedFlow,
            ) { playlist, currentTrack, currentTrackIndex, playlistSize, durationFormatted ->
                PlaylistState(
                    playlist = playlist.mapToImmutableList(TrackUiState.Companion::fromDTO),
                    currentTrack = currentTrack?.let(TrackUiState.Companion::fromDTO),
                    currentTrackIndex = currentTrackIndex,
                    playlistSize = playlistSize,
                    playlistDurationFormatted = durationFormatted,
                )
            }.distinctUntilChanged().collectLatest {
                updatePlaylistState { it }
            }
        }
    }

    private fun unsubscribeFromPlaylistUpdates() {
        playlistUpdatesJob?.cancel()
        playlistUpdatesJob = null
    }

    private fun subscribeOnDismissUpdates() {
        dismissUpdatesJob = viewModelScope.launch(Dispatchers.Default) {
            stateFlow
                .map { it.dismissState.trackPathDismissKey }
                .filter(String::isNotEmpty)
                .distinctUntilChanged()
                .collectLatest { key ->
                    val (playlistState, dismissState) = state

                    if (key.isNotEmpty())
                        currentPlaylistRepository.updateCurrentPlaylist(dismissState.playlistDismissMediator)

                    if (dismissState.trackIndexDismissMediator < playlistState.currentTrackIndex)
                        tracksRepository.updateCurrentTrackIndex(playlistState.currentTrackIndex - 1)

                    trackServiceInteractor.removeFromPlaylist(dismissState.trackIndexDismissMediator)
                }
        }
    }

    private fun unsubscribeFromDismissUpdates() {
        dismissUpdatesJob?.cancel()
        dismissUpdatesJob = null
    }

    private suspend fun startPlaylistPlayback(trackIndex: Int) {
        val (playlistState, _) = state

        playbackRepository.updateAudioStatus(AudioStatus.PLAYING)
        currentPlaylistRepository.updateCurrentPlaylist(playlistState.playlist)
        tracksRepository.updateCurrentTrackIndex(trackIndex)

        trackServiceInteractor.startPlaylistPlayback(
            nextTrack = playlistState.playlist.getOrNull(trackIndex),
            prevTrack = playlistState.currentTrack,
        )
    }

    private fun dismissTrack(index: Int) {
        val (playlistState, _) = state
        val track = playlistState.playlist[index]

        updatePlaylistDismissMediator(playlistState.playlist.exclude(index = index))
        updateTrackIndexDismissMediator(trackIndexDismissMediator = index)
        updateTrackPathDismissKey(trackPathDismissKey = track.path)
    }

    private suspend fun updateCurrentPlaylistAfterDrag(
        newPlaylist: List<Track>,
        newCurrentTrackIndex: Int,
    ) {
        tracksRepository.updateCurrentTrackIndex(newCurrentTrackIndex)
        currentPlaylistRepository.updateCurrentPlaylist(newPlaylist)

        // small delay to complete transaction and update event flow
        delay(UpdatePlaylistAfterDragDelay)
        trackServiceInteractor.updatePlaylistAfterDrag()
    }

    private fun updatePlaylistDismissMediator(
        playlistDismissMediator: ImmutableList<TrackUiState>,
    ) = updateDismissState {
        copy(playlistDismissMediator = playlistDismissMediator)
    }

    private fun updateTrackPathDismissKey(trackPathDismissKey: String) =
        updateDismissState { copy(trackPathDismissKey = trackPathDismissKey) }

    private fun updateTrackIndexDismissMediator(trackIndexDismissMediator: Int) =
        updateDismissState { copy(trackIndexDismissMediator = trackIndexDismissMediator) }
}
