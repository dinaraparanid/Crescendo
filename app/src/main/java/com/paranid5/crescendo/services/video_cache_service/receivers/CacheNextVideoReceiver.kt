package com.paranid5.crescendo.services.video_cache_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService
import com.paranid5.crescendo.services.video_cache_service.cache.cacheNewVideoAsync
import com.paranid5.crescendo.services.video_cache_service.videoCacheDataArg

fun CacheNextVideoReceiver(service: VideoCacheService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            service.mediaFileDownloader.resetDownloadStatus()
            service.cacheManager.resetCachingStatus()

            val videoData = intent.videoCacheDataArg
            service.cacheNewVideoAsync(videoData)
        }
    }