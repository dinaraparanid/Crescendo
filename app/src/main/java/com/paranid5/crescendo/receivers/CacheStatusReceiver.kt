package com.paranid5.crescendo.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.domain.caching.VideoCacheResponse
import com.paranid5.crescendo.presentation.ui.extensions.toast

class CacheStatusReceiver : BroadcastReceiver() {
    companion object {
        private const val RECEIVER_LOCATION = "com.paranid5.crescendo.receivers"
        const val Broadcast_VIDEO_CASH_COMPLETED = "$RECEIVER_LOCATION.VIDEO_CASH_COMPLETED"
        const val VIDEO_CASH_STATUS_ARG = "video_cash_status"
    }

    override fun onReceive(context: Context, intent: Intent) {
        @Suppress("DEPRECATION")
        val status = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                intent.getParcelableExtra(
                    VIDEO_CASH_STATUS_ARG,
                    VideoCacheResponse::class.java
                )

            else -> intent.getParcelableExtra(VIDEO_CASH_STATUS_ARG)
        }!!

        status.toast(context.applicationContext)
    }
}