package com.paranid5.crescendo.services.stream_service

import android.content.Intent
import com.paranid5.crescendo.R
import com.paranid5.crescendo.receivers.ServiceErrorReceiver
import com.paranid5.crescendo.services.core.notification.ErrorNotification
import com.paranid5.crescendo.services.stream_service.notification.STREAM_CHANNEL_ID
import com.paranid5.crescendo.services.stream_service.notification.STREAM_NOTIFICATION_ID

fun StreamService.showErrNotificationAndSendBroadcast(error: Throwable) {
    val errorMessage = error.message ?: getString(R.string.unknown_error)

    startForeground(
        STREAM_NOTIFICATION_ID,
        ErrorNotification(this, errorMessage, STREAM_CHANNEL_ID)
    )

    playerProvider.isStoppedWithError = true

    sendBroadcast(
        Intent(applicationContext, ServiceErrorReceiver::class.java)
            .setAction(ServiceErrorReceiver.Broadcast_SERVICE_ERROR)
            .putExtra(ServiceErrorReceiver.ERROR_MESSAGE_ARG, errorMessage)
    )
}