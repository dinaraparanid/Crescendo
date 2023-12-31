package com.paranid5.crescendo.services.stream_service.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.main.MainActivity

fun ErrorNotification(context: Context, message: String) =
    NotificationBuilderCompat(context)
        .setSmallIcon(R.drawable.save_icon)
        .setContentIntent(
            PendingIntent.getActivity(
                context.applicationContext,
                0,
                Intent(context.applicationContext, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        .setContentTitle(message)
        .setAutoCancel(true)
        .setShowWhen(false)
        .build()

@Suppress("DEPRECATION")
private fun NotificationBuilderCompat(context: Context) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
        Notification.Builder(
            context.applicationContext,
            STREAM_CHANNEL_ID
        )

    else -> Notification.Builder(context.applicationContext)
}