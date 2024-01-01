package com.paranid5.crescendo.services.track_service.playback

import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.services.track_service.TrackService
import kotlinx.coroutines.launch

fun TrackService.startResumingAsync() =
    serviceScope.launch { playerProvider.startResuming() }

fun TrackService.playPlaylistAsync(playlist: List<Track>, trackIndex: Int) =
    serviceScope.launch { playerProvider.playPlaylist(playlist, trackIndex) }

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

fun TrackService.addTrackToPlaylistAsync(track: Track) =
    serviceScope.launch { playerProvider.addTrackToPlaylist(track) }

fun TrackService.removeTrackFromPlaylistAsync(index: Int) =
    serviceScope.launch { playerProvider.removeTrackFromPlaylist(index) }

fun TrackService.replacePlaylistAsync(newPlaylist: List<Track>, newCurrentTrackIndex: Int) =
    serviceScope.launch { playerProvider.replacePlaylist(newPlaylist, newCurrentTrackIndex) }