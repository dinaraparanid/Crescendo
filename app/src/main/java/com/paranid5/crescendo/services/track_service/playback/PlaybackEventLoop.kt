package com.paranid5.crescendo.services.track_service.playback

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Tuple4
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.services.track_service.TrackService
import com.paranid5.crescendo.services.track_service.showErrNotificationAndSendBroadcast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

suspend fun TrackService.startPlaybackEventLoop() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        playerProvider.playbackEventFlow
            .combine(ArgsFlow(this@startPlaybackEventLoop)) { event, (trackInd, position, playlist) ->
                Tuple4(event, trackInd, position, playlist)
            }
            .distinctUntilChanged { (e1, _, _, _), (e2, _, _, _) -> e1 == e2 }
            .collectLatest { (event, trackInd, position, playlist) ->
                onEvent(event, trackInd, position, playlist)
            }
    }

private fun ArgsFlow(service: TrackService) =
    combine(
        service.playerProvider.currentTrackIndexFlow,
        service.playerProvider.tracksPlaybackPositionFlow,
        service.playerProvider.currentPlaylistFlow,
    ) { trackIndex, position, playlist ->
        Triple(trackIndex, position, playlist)
    }.distinctUntilChanged()

private suspend inline fun TrackService.onEvent(
    event: PlaybackEvent,
    trackInd: Int,
    position: Long,
    playlist: List<com.paranid5.crescendo.core.common.tracks.Track>,
) = when (event) {
    is PlaybackEvent.StartSamePlaylist -> onPlayPlaylist(
        playlist = playlist,
        trackIndex = trackInd,
        initialPosition = position
    )

    is PlaybackEvent.StartNewPlaylist -> onStartNewPlaylist(
        newPlaylist = playlist,
        newTrackIndex = trackInd,
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

    is PlaybackEvent.AddTrackToPlaylist -> onAddTrackToPlaylist(event.track)

    is PlaybackEvent.RemoveTrackFromPlaylist -> onRemoveTrackFromPlaylist(event.index)

    is PlaybackEvent.ReplacePlaylist -> onReplacePlaylist(
        newCurrentTrackIndex = trackInd,
        newCurrentPlaylist = playlist
    )
}

private fun TrackService.onPlayPlaylist(
    playlist: List<com.paranid5.crescendo.core.common.tracks.Track>,
    trackIndex: Int,
    initialPosition: Long = 0
) {
    if (playlist.isEmpty())
        return showErrNotificationAndSendBroadcast(Exception(getString(R.string.playlist_empty_err)))

    playerProvider.resetAudioSessionIdIfNotPlaying()

    playerProvider.playPlaylistViaPlayer(playlist, trackIndex, initialPosition)
}

private fun TrackService.onStartNewPlaylist(
    newPlaylist: List<com.paranid5.crescendo.core.common.tracks.Track>,
    newTrackIndex: Int,
) = onPlayPlaylist(
    playlist = newPlaylist,
    trackIndex = newTrackIndex,
)

private suspend inline fun TrackService.onPause() {
    playerProvider.setTracksPlaybackPosition(playerProvider.currentPosition)
    playerProvider.pausePlayer()
}

private fun TrackService.onResume(
    playlist: List<com.paranid5.crescendo.core.common.tracks.Track>,
    trackIndex: Int,
    initialPosition: Long
) = when {
    playerProvider.isStoppedWithError -> {
        onPlayPlaylist(
            playlist = playlist,
            trackIndex = trackIndex,
            initialPosition = initialPosition
        )

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

private fun TrackService.onAddTrackToPlaylist(track: com.paranid5.crescendo.core.common.tracks.Track) =
    playerProvider.addTrackToPlaylistViaPlayer(track)

private fun TrackService.onRemoveTrackFromPlaylist(index: Int) {
    playerProvider.removeTrackViaPlayer(index)
}

private fun TrackService.onReplacePlaylist(
    newCurrentTrackIndex: Int,
    newCurrentPlaylist: List<com.paranid5.crescendo.core.common.tracks.Track>
) {
    playerProvider.replacePlaylistViaPlayer(newCurrentPlaylist, newCurrentTrackIndex)
}