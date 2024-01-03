package com.paranid5.crescendo.services.video_cache_service

import android.content.Intent
import com.paranid5.crescendo.R
import com.paranid5.crescendo.receivers.ServiceErrorReceiver
import com.paranid5.crescendo.services.core.notification.ErrorNotification
import com.paranid5.crescendo.services.video_cache_service.notification.VIDEO_CACHE_NOTIFICATION_ID
import com.paranid5.crescendo.services.video_cache_service.notification.VIDEO_CACHE_CHANNEL_ID

fun VideoCacheService.reportExtractionError(error: Throwable) {
    val errorMessage = error.message ?: getString(R.string.unknown_error)

    startForeground(
        VIDEO_CACHE_NOTIFICATION_ID,
        ErrorNotification(this, errorMessage, VIDEO_CACHE_CHANNEL_ID)
    )

    sendBroadcast(
        Intent(applicationContext, ServiceErrorReceiver::class.java)
            .setAction(ServiceErrorReceiver.Broadcast_SERVICE_ERROR)
            .putExtra(ServiceErrorReceiver.ERROR_MESSAGE_ARG, errorMessage)
    )
}