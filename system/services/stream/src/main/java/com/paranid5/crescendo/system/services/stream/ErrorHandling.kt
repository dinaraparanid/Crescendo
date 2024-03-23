package com.paranid5.crescendo.system.services.stream

import android.content.Intent
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.system.receivers.ServiceErrorReceiver
import com.paranid5.crescendo.system.services.stream.notification.STREAM_CHANNEL_ID
import com.paranid5.crescendo.system.services.stream.notification.STREAM_NOTIFICATION_ID
import com.paranid5.system.services.common.notification.ErrorNotification

internal fun StreamService.showErrNotificationAndSendBroadcast(error: Throwable) {
    val errorMessage = error.message ?: getString(R.string.unknown_error)

    startForeground(
        STREAM_NOTIFICATION_ID,
        ErrorNotification(
            this,
            errorMessage,
            STREAM_CHANNEL_ID
        )
    )

    playerProvider.isStoppedWithError = true

    sendBroadcast(
        Intent(applicationContext, ServiceErrorReceiver::class.java)
            .setAction(ServiceErrorReceiver.Broadcast_SERVICE_ERROR)
            .putExtra(ServiceErrorReceiver.ERROR_MESSAGE_ARG, errorMessage)
    )
}