package com.paranid5.crescendo.system.services.video_cache.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService
import com.paranid5.crescendo.system.services.video_cache.cache.cacheNewVideoAsync
import com.paranid5.crescendo.system.services.video_cache.videoCacheDataArg

internal fun CacheNextVideoReceiver(service: VideoCacheService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            service.mediaFileDownloader.resetDownloadStatus()
            service.cacheManager.resetCachingStatus()

            val videoData = intent.videoCacheDataArg
            service.cacheNewVideoAsync(videoData)
        }
    }