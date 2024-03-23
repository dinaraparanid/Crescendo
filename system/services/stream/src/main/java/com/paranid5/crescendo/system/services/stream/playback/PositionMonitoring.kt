package com.paranid5.crescendo.system.services.stream.playback

import com.paranid5.crescendo.system.services.stream.StreamService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PLAYBACK_UPDATE_COOLDOWN = 500L

private lateinit var playbackPosMonitorTask: Job
private lateinit var playbackPosCacheTask: Job

internal fun StreamService.startPlaybackPositionMonitoringAsync() =
    serviceScope.launch { startPlaybackPositionMonitoring() }

internal fun StreamService.startPlaybackPositionMonitoring() {
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