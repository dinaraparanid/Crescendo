package com.paranid5.crescendo.services.stream_service.playback

import com.paranid5.crescendo.services.stream_service.StreamService2
import kotlinx.coroutines.launch

fun StreamService2.resumeAsync() =
    serviceScope.launch { playerProvider.resume() }

fun StreamService2.pauseAsync() =
    serviceScope.launch { playerProvider.pause() }

fun StreamService2.seekToAsync(position: Long) =
    serviceScope.launch { playerProvider.seekTo(position) }

fun StreamService2.seekTenSecsForwardAsync() =
    serviceScope.launch { playerProvider.seekTenSecsForward() }

fun StreamService2.seekTenSecsBackAsync() =
    serviceScope.launch { playerProvider.seekTenSecsBack() }