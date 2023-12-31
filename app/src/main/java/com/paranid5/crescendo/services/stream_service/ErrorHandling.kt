package com.paranid5.crescendo.services.stream_service

import android.content.Intent
import com.paranid5.crescendo.R
import com.paranid5.crescendo.receivers.PlaybackErrorReceiver
import com.paranid5.crescendo.services.stream_service.notification.NOTIFICATION_ID
import com.paranid5.crescendo.services.stream_service.notification.ErrorNotification

fun StreamService2.sendErrorBroadcast(error: Throwable) {
    val errorMessage = error.message ?: getString(R.string.unknown_error)
    startForeground(NOTIFICATION_ID, ErrorNotification(this, errorMessage))
    playerProvider.isStoppedWithError = true

    sendBroadcast(
        Intent(applicationContext, PlaybackErrorReceiver::class.java)
            .setAction(PlaybackErrorReceiver.Broadcast_PLAYBACK_ERROR)
            .putExtra(
                PlaybackErrorReceiver.ERROR_MESSAGE_ARG,
                errorMessage
            )
    )
}