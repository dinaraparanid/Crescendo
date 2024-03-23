package com.paranid5.crescendo.system.services.track.playback

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.system.services.track.TrackService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal fun TrackService.startResumingAsync() =
    serviceScope.launch {
        delay(500)
        playerProvider.startResuming()
    }

internal fun TrackService.playPlaylistAsync() =
    serviceScope.launch {
        delay(500)
        playerProvider.playPlaylist()
    }

internal fun TrackService.resumeAsync() =
    serviceScope.launch { playerProvider.resume() }

internal fun TrackService.pauseAsync() =
    serviceScope.launch { playerProvider.pause() }

internal fun TrackService.seekToAsync(position: Long) =
    serviceScope.launch { playerProvider.seekTo(position) }

internal fun TrackService.seekToNextTrackAsync() =
    serviceScope.launch { playerProvider.seekToNextTrack() }

internal fun TrackService.seekToPrevTrackAsync() =
    serviceScope.launch { playerProvider.seekToPrevTrack() }

internal fun TrackService.addTrackToPlaylistAsync(track: Track) =
    serviceScope.launch { playerProvider.addTrackToPlaylist(track) }

internal fun TrackService.removeTrackFromPlaylistAsync(index: Int) =
    serviceScope.launch { playerProvider.removeTrackFromPlaylist(index) }

internal fun TrackService.replacePlaylistAsync() =
    serviceScope.launch { playerProvider.replacePlaylist() }

internal fun TrackService.restartPlayerAsync() =
    serviceScope.launch { playerProvider.restartPlayer() }

internal fun TrackService.updateCurrentTrackIndexAsync() =
    serviceScope.launch { playerProvider.updateCurrentTrackIndex() }