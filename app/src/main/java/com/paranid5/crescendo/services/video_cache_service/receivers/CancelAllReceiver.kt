package com.paranid5.crescendo.services.video_cache_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService

fun CancelAllReceiver(service: VideoCacheService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            service.mediaFileDownloader.onCanceledAll()
            service.cacheManager.onCanceledAll()
        }
    }