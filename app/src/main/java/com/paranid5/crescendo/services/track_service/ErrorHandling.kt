package com.paranid5.crescendo.services.track_service

import android.content.Intent
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.receivers.ServiceErrorReceiver
import com.paranid5.crescendo.services.core.notification.ErrorNotification
import com.paranid5.crescendo.services.track_service.notification.TRACKS_CHANNEL_ID
import com.paranid5.crescendo.services.track_service.notification.TRACKS_NOTIFICATION_ID

fun TrackService.showErrNotificationAndSendBroadcast(error: Throwable) {
    val errorMessage = error.message ?: getString(R.string.unknown_error)

    startForeground(
        TRACKS_NOTIFICATION_ID,
        ErrorNotification(this, errorMessage, TRACKS_CHANNEL_ID)
    )

    playerProvider.isStoppedWithError = true

    sendBroadcast(
        Intent(applicationContext, ServiceErrorReceiver::class.java)
            .setAction(ServiceErrorReceiver.Broadcast_SERVICE_ERROR)
            .putExtra(ServiceErrorReceiver.ERROR_MESSAGE_ARG, errorMessage)
    )
}