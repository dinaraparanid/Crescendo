package com.paranid5.crescendo.system.services.track.playback

import com.paranid5.crescendo.system.services.track.TrackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PLAYBACK_UPDATE_COOLDOWN = 500L

private lateinit var playbackPosMonitorTask: Job
private lateinit var playbackPosCacheTask: Job

internal fun TrackService.startPlaybackPositionMonitoringAsync() =
    serviceScope.launch { startPlaybackPositionMonitoring() }

internal fun TrackService.startPlaybackPositionMonitoring() {
    playbackPosMonitorTask = serviceScope.launch {
        while (playerProvider.isPlaying) {
            playerProvider.fetchPositionFromPlayer()
            delay(PLAYBACK_UPDATE_COOLDOWN)
        }
    }

    playbackPosCacheTask = serviceScope.launch(Dispatchers.IO) {
        while (playerProvider.isPlaying) {
            playerProvider.storePlaybackPosition()
            delay(PLAYBACK_UPDATE_COOLDOWN)
        }
    }
}

internal fun stopPlaybackPositionMonitoring() {
    playbackPosMonitorTask.cancel()
    playbackPosCacheTask.cancel()
}
