package com.paranid5.crescendo.services.video_cache_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService

fun CancelCurrentVideoReceiver(service: VideoCacheService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println("CANCEL CUR RECEIVED")
            service.mediaFileDownloader.onCanceledCurrent()
            service.cacheManager.onCanceledCurrent()
        }
    }