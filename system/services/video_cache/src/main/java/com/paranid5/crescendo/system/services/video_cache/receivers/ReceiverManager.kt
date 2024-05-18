package com.paranid5.crescendo.system.services.video_cache.receivers

import com.paranid5.crescendo.system.common.broadcast.VideoCacheServiceBroadcasts
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService
import com.paranid5.crescendo.utils.extensions.registerReceiverCompat

@Suppress("IncorrectFormatting")
internal fun VideoCacheService.registerReceivers() {
    registerReceiverCompat(cacheNextVideoReceiver, VideoCacheServiceBroadcasts.Broadcast_CACHE_NEXT_VIDEO)
    registerReceiverCompat(cancelCurrentVideoReceiver, VideoCacheServiceBroadcasts.Broadcast_CANCEL_CUR_VIDEO)
    registerReceiverCompat(cancelAllReceiver, VideoCacheServiceBroadcasts.Broadcast_CANCEL_ALL)
}

internal fun VideoCacheService.unregisterReceivers() {
    unregisterReceiver(cacheNextVideoReceiver)
    unregisterReceiver(cancelCurrentVideoReceiver)
    unregisterReceiver(cancelAllReceiver)
}