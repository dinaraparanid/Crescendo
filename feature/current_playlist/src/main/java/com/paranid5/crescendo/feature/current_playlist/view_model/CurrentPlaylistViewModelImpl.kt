package com.paranid5.crescendo.feature.current_playlist.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.current_playlist.currentPlaylistDurationFormattedFlow
import com.paranid5.crescendo.domain.current_playlist.currentPlaylistSizeFlow
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistState.DismissState
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistState.PlaylistState
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
import com.paranid5.crescendo.system.services.track.startPlaylistPlayback
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.extensions.exclude
import com.paranid5.crescendo.utils.extensions.mapToImmutableList
import com.paranid5.crescendo.utils.extensions.sideEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class CurrentPlaylistViewModelImpl(
    private val currentPlaylistRepository: CurrentPlaylistRepository,
    private val playbackRepository: PlaybackRepository,
    private val tracksRepository: TracksRepository,
    private val trackServiceInteractor: TrackServiceInteractor
) : ViewModel(),
    CurrentPlaylistViewModel,
    StatePublisher<CurrentPlaylistState> {
    companion object {
        private const val UpdatePlaylistAfterDragDelay = 500L
    }

    private var playlistUpdatesJob: Job? = null
    private var dismissUpdatesJob: Job? = null

    private val _stateFlow = MutableStateFlow(CurrentPlaylistState())

    override val stateFlow =
        _stateFlow.asStateFlow()

    override fun updateState(func: CurrentPlaylistState.() -> CurrentPlaylistState) =
        _stateFlow.update(func)

    private inline fun updateDismissState(crossinline func: DismissState.() -> DismissState) =
        updateState { copy(dismissState = func(dismissState)) }

    private inline fun updatePlaylistState(crossinline func: PlaylistState.() -> PlaylistState) =
        updateState { copy(playlistState = func(playlistState)) }

    override fun onUiIntent(intent: CurrentPlaylistUiIntent) {
        when (intent) {
            is CurrentPlaylistUiIntent.Lifecycle -> onLifecycleUiIntent(intent)
            is CurrentPlaylistUiIntent.Playlist -> onPlaylistUiIntent(intent)
            is CurrentPlaylistUiIntent.Screen -> onScreenUiIntent(intent)
        }
    }

    private fun onLifecycleUiIntent(intent: CurrentPlaylistUiIntent.Lifecycle) = when (intent) {
        is CurrentPlaylistUiIntent.Lifecycle.OnStart -> {
            subscribeOnPlaylistUpdates()
            subscribeOnDismissUpdates()
        }

        is CurrentPlaylistUiIntent.Lifecycle.OnStop -> {
            unsubscribeFromPlaylistUpdates()
            unsubscribeFromDismissUpdates()
        }
    }

    private fun onPlaylistUiIntent(intent: CurrentPlaylistUiIntent.Playlist) = when (intent) {
        is CurrentPlaylistUiIntent.Playlist.AddTrackToPlaylist ->
            addToPlaylist(track = intent.track)

        is CurrentPlaylistUiIntent.Playlist.DismissTrack ->
            dismissTrack(index = intent.index)

        is CurrentPlaylistUiIntent.Playlist.StartPlaylistPlayback -> viewModelScope.sideEffect {
            startPlaylistPlayback(trackIndex = intent.trackIndex)
        }

        is CurrentPlaylistUiIntent.Playlist.UpdateAfterDrag -> viewModelScope.sideEffect {
            updateCurrentPlaylistAfterDrag(
                newPlaylist = intent.newPlaylist,
                newCurrentTrackIndex = intent.newCurrentTrackIndex,
            )
        }
    }

    private fun onScreenUiIntent(intent: CurrentPlaylistUiIntent.Screen) = when (intent) {
        is CurrentPlaylistUiIntent.Screen.ClearScreenEffect -> updateState {
            copy(screenEffect = null)
        }

        is CurrentPlaylistUiIntent.Screen.ShowMetaEditor -> updateState {
            copy(screenEffect = CurrentPlaylistScreenEffect.ShowMetaEditor)
        }

        is CurrentPlaylistUiIntent.Screen.ShowTrimmer -> updateState {
            copy(screenEffect = CurrentPlaylistScreenEffect.ShowTrimmer(trackUri = intent.trackUri))
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

        playbackRepository.updateAudioStatus(PlaybackStatus.PLAYING)
        currentPlaylistRepository.updateCurrentPlaylist(playlistState.playlist)
        tracksRepository.updateCurrentTrackIndex(trackIndex)

        trackServiceInteractor.startPlaylistPlayback(
            nextTrack = playlistState.playlist.getOrNull(trackIndex),
            prevTrack = playlistState.currentTrack,
        )
    }

    private fun dismissTrack(index: Int) {
        val playlistState = state.playlistState
        val track = playlistState.playlist[index]

        updatePlaylistDismissMediator(playlistState.playlist.exclude(index = index))
        updateTrackIndexDismissMediator(trackIndexDismissMediator = index)
        updateTrackPathDismissKey(trackPathDismissKey = track.path)
    }

    private fun addToPlaylist(track: Track) {
        val defaultTrack = DefaultTrack(track)
        trackServiceInteractor.addToPlaylist(defaultTrack)
        viewModelScope.launch { currentPlaylistRepository.addTrackToPlaylist(defaultTrack) }
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
