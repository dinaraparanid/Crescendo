package com.paranid5.crescendo.services.track_service.playback

import com.paranid5.crescendo.services.track_service.TrackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PLAYBACK_UPDATE_COOLDOWN = 500L

private lateinit var playbackPosMonitorTask: Job
private lateinit var playbackPosCacheTask: Job

fun TrackService.startPlaybackPositionMonitoringAsync() =
    serviceScope.launch { startPlaybackPositionMonitoring() }

fun TrackService.startPlaybackPositionMonitoring() {
    playbackPosMonitorTask = serviceScope.launch {
        while (playerProvider.isPlaying) {
            playerProvider.updateCurrentPosition()
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