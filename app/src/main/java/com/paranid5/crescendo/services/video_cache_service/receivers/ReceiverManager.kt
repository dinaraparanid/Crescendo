package com.paranid5.crescendo.services.video_cache_service.receivers

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.paranid5.crescendo.domain.utils.extensions.registerReceiverCompat
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService

@Suppress("IncorrectFormatting")
fun VideoCacheService.registerReceivers() {
    registerReceiverCompat(cacheNextVideoReceiver, VideoCacheService.Broadcast_CACHE_NEXT_VIDEO)
    registerReceiverCompat(cancelCurrentVideoReceiver, VideoCacheService.Broadcast_CANCEL_CUR_VIDEO)
    registerReceiverCompat(cancelAllReceiver, VideoCacheService.Broadcast_CANCEL_ALL)
}

fun VideoCacheService.unregisterReceivers() =
    LocalBroadcastManager.getInstance(this).run {
        unregisterReceiver(cacheNextVideoReceiver)
        unregisterReceiver(cancelCurrentVideoReceiver)
        unregisterReceiver(cancelAllReceiver)
    }