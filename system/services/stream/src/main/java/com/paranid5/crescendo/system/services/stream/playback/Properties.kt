package com.paranid5.crescendo.system.services.stream.playback

import com.paranid5.crescendo.system.services.stream.StreamService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal fun StreamService.startResumingAsync() =
    serviceScope.launch {
        delay(500)
        playerProvider.startResuming()
    }

internal fun StreamService.startStreamAsync(url: String) =
    serviceScope.launch {
        delay(500)
        playerProvider.startStream(url)
    }

internal fun StreamService.resumeAsync() =
    serviceScope.launch { playerProvider.resume() }

internal fun StreamService.pauseAsync() =
    serviceScope.launch { playerProvider.pause() }

internal fun StreamService.seekToAsync(position: Long) =
    serviceScope.launch { playerProvider.seekTo(position) }

internal fun StreamService.seekTenSecsForwardAsync() =
    serviceScope.launch { playerProvider.seekTenSecsForward() }

internal fun StreamService.seekTenSecsBackAsync() =
    serviceScope.launch { playerProvider.seekTenSecsBack() }

internal fun StreamService.restartPlayerAsync() =
    serviceScope.launch { playerProvider.restartPlayer() }