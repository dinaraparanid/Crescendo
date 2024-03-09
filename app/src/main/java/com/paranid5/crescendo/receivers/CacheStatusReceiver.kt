package com.paranid5.crescendo.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.core.media.caching.VideoCacheResponse
import com.paranid5.crescendo.core.media.caching.toast

class CacheStatusReceiver : BroadcastReceiver() {
    companion object {
        private const val RECEIVER_LOCATION = "com.paranid5.crescendo.receivers"
        const val Broadcast_VIDEO_CACHE_COMPLETED = "$RECEIVER_LOCATION.VIDEO_CACHE_COMPLETED"
        const val VIDEO_CACHE_STATUS_ARG = "video_cache_status"
    }

    override fun onReceive(context: Context, intent: Intent) {
        @Suppress("DEPRECATION")
        val response = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                intent.getParcelableExtra(
                    VIDEO_CACHE_STATUS_ARG,
                    VideoCacheResponse::class.java
                )

            else -> intent.getParcelableExtra(VIDEO_CACHE_STATUS_ARG)
        }!!

        toast(response, context.applicationContext)
    }
}