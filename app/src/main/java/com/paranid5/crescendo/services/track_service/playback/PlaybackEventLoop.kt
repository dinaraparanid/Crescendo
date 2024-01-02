package com.paranid5.crescendo.services.track_service.playback

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Tuple4
import arrow.core.Tuple5
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.services.track_service.TrackService
import com.paranid5.crescendo.services.track_service.sendErrorBroadcast
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

suspend fun PlaybackEventLoop(service: TrackService): MutableSharedFlow<PlaybackEvent> {
    val playbackEventFlow = MutableSharedFlow<PlaybackEvent>()

    service.serviceScope.launch {
        service.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            service.playerProvider.incrementPlaybackEventLoopInitSteps()

            playbackEventFlow
                .combine(ArgsFlow(service)) { event, (trackInd, position, isPlaying, playlist) ->
                    Tuple5(event, trackInd, position, isPlaying, playlist)
                }
                .distinctUntilChanged { (e1, _, _, _, _), (e2, _, _, _, _) -> e1 == e2 }
                .collectLatest { (event, trackInd, position, isPlaying, playlist) ->
                    service.onEvent(event, trackInd, position, isPlaying, playlist)
                }
        }
    }

    return playbackEventFlow
}

private fun ArgsFlow(service: TrackService) =
    combine(
        service.playerProvider.currentTrackIndexFlow,
        service.playerProvider.tracksPlaybackPositionFlow,
        service.playerProvider.isPlayingState,
        service.playerProvider.currentPlaylistFlow,
    ) { trackIndex, position, isPlaying, playlist ->
        Tuple4(trackIndex, position, isPlaying, playlist)
    }.distinctUntilChanged()

private suspend inline fun TrackService.onEvent(
    event: PlaybackEvent,
    trackInd: Int,
    position: Long,
    isPlaying: Boolean,
    playlist: List<Track>,
) = when (event) {
    is PlaybackEvent.StartSamePlaylist -> onPlayPlaylist(
        playlist = playlist,
        trackIndex = trackInd,
        initialPosition = position
    )

    is PlaybackEvent.StartNewPlaylist -> onStartNewPlaylist(
        currentPlaylist = playlist,
        currentTrackIndex = trackInd,
        currentPosition = position,
        newPlaylist = event.playlist,
        newTrackIndex = event.trackIndex,
        newPosition = event.initialPosition,
        isPlaying = isPlaying
    )

    is PlaybackEvent.Pause -> onPause()

    is PlaybackEvent.Resume -> onResume(
        playlist = playlist,
        trackIndex = trackInd,
        initialPosition = position
    )

    is PlaybackEvent.SeekTo -> onSeekTo(event.position)

    is PlaybackEvent.SeekToPrevTrack -> onSeekToPreviousTrack(
        playlistSize = playlist.size
    )

    is PlaybackEvent.SeekToNextTrack -> onSeekToNextTrack()

    is PlaybackEvent.AddTrackToPlaylist -> onAddTrackToPlaylist(
        track = event.track,
        currentPlaylist = playlist
    )

    is PlaybackEvent.RemoveTrackFromPlaylist -> onRemoveTrackFromPlaylist(
        index = event.index,
        currentPlaylist = playlist
    )

    is PlaybackEvent.ReplacePlaylist -> onReplacePlaylist(
        newCurrentPlaylist = event.playlist,
        newCurrentTrackIndex = event.index
    )
}

private suspend inline fun TrackService.onPlayPlaylist(
    playlist: List<Track>,
    trackIndex: Int,
    initialPosition: Long
) {
    if (playlist.isEmpty())
        return sendErrorBroadcast(Exception(getString(R.string.playlist_empty_err)))

    playerProvider.resetAudioSessionIdIfNotPlaying()
    playerProvider.setCurrentTrackIndex(trackIndex)
    playerProvider.setCurrentPlaylist(playlist)
    playerProvider.playPlaylistViaPlayer(playlist, trackIndex, initialPosition)
}

private suspend inline fun TrackService.onStartNewPlaylist(
    currentPlaylist: List<Track>,
    currentTrackIndex: Int,
    currentPosition: Long,
    newPlaylist: List<Track>,
    newTrackIndex: Int,
    newPosition: Long,
    isPlaying: Boolean,
) {
    val currentTrackPath = currentPlaylist.getOrNull(currentTrackIndex)?.path
    val newTrackPath = newPlaylist[newTrackIndex].path

    when {
        newTrackPath == currentTrackPath && isPlaying -> onPause()

        newTrackPath == currentTrackPath ->
            onPlayPlaylist(newPlaylist, newTrackIndex, currentPosition)

        else -> onPlayPlaylist(newPlaylist, newTrackIndex, newPosition)
    }
}

private suspend fun TrackService.onPause() {
    playerProvider.setTracksPlaybackPosition(playerProvider.currentPosition)
    playerProvider.pausePlayer()
}

private suspend inline fun TrackService.onResume(
    playlist: List<Track>,
    trackIndex: Int,
    initialPosition: Long
) = when {
    playerProvider.isStoppedWithError -> {
        onPlayPlaylist(playlist, trackIndex, initialPosition)
        playerProvider.isStoppedWithError = false
    }

    else -> playerProvider.resumePlayer()
}

private fun TrackService.onSeekTo(position: Long) {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    playerProvider.seekToViaPlayer(position)
}

private suspend inline fun TrackService.onSeekToPreviousTrack(playlistSize: Int) {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    val newCurTrackInd = playerProvider.seekToPreviousTrackViaPlayer(playlistSize)
    playerProvider.setCurrentTrackIndex(newCurTrackInd)
}

private suspend inline fun TrackService.onSeekToNextTrack() {
    playerProvider.resetAudioSessionIdIfNotPlaying()
    val newCurTrackInd = playerProvider.seekToNextTrackViaPlayer()
    playerProvider.setCurrentTrackIndex(newCurTrackInd)
}

private suspend inline fun TrackService.onAddTrackToPlaylist(
    track: Track,
    currentPlaylist: List<Track>
) {
    playerProvider.setCurrentPlaylist(currentPlaylist + track)
    playerProvider.addTrackToPlaylistViaPlayer(track)
}

private suspend inline fun TrackService.onRemoveTrackFromPlaylist(
    index: Int,
    currentPlaylist: List<Track>
) {
    val newPlaylist = currentPlaylist.run { take(index) + drop(index + 1) }
    val newCurrentTrackIndex = playerProvider.removeTrackViaPlayer(index)
    playerProvider.setCurrentTrackIndex(newCurrentTrackIndex)
    playerProvider.setCurrentPlaylist(newPlaylist)
}

private suspend inline fun TrackService.onReplacePlaylist(
    newCurrentTrackIndex: Int,
    newCurrentPlaylist: List<Track>
) {
    playerProvider.setCurrentTrackIndex(newCurrentTrackIndex)
    playerProvider.setCurrentPlaylist(newCurrentPlaylist)
    playerProvider.replacePlaylistViaPlayer(newCurrentPlaylist, newCurrentTrackIndex)
}