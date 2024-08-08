package com.paranid5.system.services.common.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.system.common.intent.mainActivityIntent

fun ErrorNotification(context: Context, message: String, channelId: String) =
    NotificationBuilderCompat(context, channelId)
        .setSmallIcon(R.drawable.ic_save)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                context.mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        .setContentTitle(message)
        .setAutoCancel(true)
        .setShowWhen(false)
        .build()

@Suppress("DEPRECATION")
private fun NotificationBuilderCompat(context: Context, channelId: String) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
        Notification.Builder(context, channelId)

    else -> Notification.Builder(context)
}