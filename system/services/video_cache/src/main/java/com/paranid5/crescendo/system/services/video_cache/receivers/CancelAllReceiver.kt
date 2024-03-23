package com.paranid5.crescendo.system.services.video_cache.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService

fun CancelAllReceiver(service: VideoCacheService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            service.mediaFileDownloader.onCanceledAll()
            service.cacheManager.onCanceledAll()
            service.videoQueueManager.onCanceledAll()
        }
    }