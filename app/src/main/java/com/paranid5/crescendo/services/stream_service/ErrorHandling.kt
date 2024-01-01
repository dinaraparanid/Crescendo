package com.paranid5.crescendo.services.stream_service

import android.content.Intent
import com.paranid5.crescendo.R
import com.paranid5.crescendo.receivers.PlaybackErrorReceiver
import com.paranid5.crescendo.services.core.notification.ErrorNotification
import com.paranid5.crescendo.services.stream_service.notification.STREAM_CHANNEL_ID
import com.paranid5.crescendo.services.stream_service.notification.STREAM_NOTIFICATION_ID

fun StreamService.sendErrorBroadcast(error: Throwable) {
    val errorMessage = error.message ?: getString(R.string.unknown_error)

    startForeground(
        STREAM_NOTIFICATION_ID,
        ErrorNotification(this, errorMessage, STREAM_CHANNEL_ID)
    )

    playerProvider.isStoppedWithError = true

    sendBroadcast(
        Intent(applicationContext, PlaybackErrorReceiver::class.java)
            .setAction(PlaybackErrorReceiver.Broadcast_PLAYBACK_ERROR)
            .putExtra(PlaybackErrorReceiver.ERROR_MESSAGE_ARG, errorMessage)
    )
}