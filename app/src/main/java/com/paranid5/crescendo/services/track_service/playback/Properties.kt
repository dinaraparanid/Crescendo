package com.paranid5.crescendo.services.track_service.playback

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.services.track_service.TrackService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun TrackService.startResumingAsync() =
    serviceScope.launch {
        delay(500)
        playerProvider.startResuming()
    }

fun TrackService.playPlaylistAsync() =
    serviceScope.launch {
        delay(500)
        playerProvider.playPlaylist()
    }

fun TrackService.resumeAsync() =
    serviceScope.launch { playerProvider.resume() }

fun TrackService.pauseAsync() =
    serviceScope.launch { playerProvider.pause() }

fun TrackService.seekToAsync(position: Long) =
    serviceScope.launch { playerProvider.seekTo(position) }

fun TrackService.seekToNextTrackAsync() =
    serviceScope.launch { playerProvider.seekToNextTrack() }

fun TrackService.seekToPrevTrackAsync() =
    serviceScope.launch { playerProvider.seekToPrevTrack() }

fun TrackService.addTrackToPlaylistAsync(track: com.paranid5.crescendo.core.common.tracks.Track) =
    serviceScope.launch { playerProvider.addTrackToPlaylist(track) }

fun TrackService.removeTrackFromPlaylistAsync(index: Int) =
    serviceScope.launch { playerProvider.removeTrackFromPlaylist(index) }

fun TrackService.replacePlaylistAsync() =
    serviceScope.launch { playerProvider.replacePlaylist() }

fun TrackService.restartPlayerAsync() =
    serviceScope.launch { playerProvider.restartPlayer() }

fun TrackService.updateCurrentTrackIndexAsync() =
    serviceScope.launch { playerProvider.updateCurrentTrackIndex() }