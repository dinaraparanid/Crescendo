package com.paranid5.crescendo.services.stream_service.playback

import com.paranid5.crescendo.services.stream_service.StreamService
import kotlinx.coroutines.launch

fun StreamService.startResumingAsync() =
    serviceScope.launch { playerProvider.startResuming() }

fun StreamService.storeAndPlayStreamAsync(url: String) =
    serviceScope.launch { playerProvider.storeAndPlayStream(url) }

fun StreamService.resumeAsync() =
    serviceScope.launch { playerProvider.resume() }

fun StreamService.pauseAsync() =
    serviceScope.launch { playerProvider.pause() }

fun StreamService.seekToAsync(position: Long) =
    serviceScope.launch { playerProvider.seekTo(position) }

fun StreamService.seekTenSecsForwardAsync() =
    serviceScope.launch { playerProvider.seekTenSecsForward() }

fun StreamService.seekTenSecsBackAsync() =
    serviceScope.launch { playerProvider.seekTenSecsBack() }