package com.paranid5.crescendo.system.services.video_cache.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService

fun CancelCurrentVideoReceiver(service: VideoCacheService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            service.mediaFileDownloader.onCanceledCurrent()
            service.cacheManager.onCanceledCurrent()
        }
    }