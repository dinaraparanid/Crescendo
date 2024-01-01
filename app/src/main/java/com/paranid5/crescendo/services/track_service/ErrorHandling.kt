package com.paranid5.crescendo.services.track_service

import android.content.Intent
import com.paranid5.crescendo.R
import com.paranid5.crescendo.receivers.PlaybackErrorReceiver
import com.paranid5.crescendo.services.core.notification.ErrorNotification
import com.paranid5.crescendo.services.track_service.notification.TRACKS_CHANNEL_ID
import com.paranid5.crescendo.services.track_service.notification.TRACKS_NOTIFICATION_ID

fun TrackService.sendErrorBroadcast(error: Throwable) {
    val errorMessage = error.message ?: getString(R.string.unknown_error)

    startForeground(
        TRACKS_NOTIFICATION_ID,
        ErrorNotification(this, errorMessage, TRACKS_CHANNEL_ID)
    )

    playerProvider.isStoppedWithError = true

    sendBroadcast(
        Intent(applicationContext, PlaybackErrorReceiver::class.java)
            .setAction(PlaybackErrorReceiver.Broadcast_PLAYBACK_ERROR)
            .putExtra(PlaybackErrorReceiver.ERROR_MESSAGE_ARG, errorMessage)
    )
}