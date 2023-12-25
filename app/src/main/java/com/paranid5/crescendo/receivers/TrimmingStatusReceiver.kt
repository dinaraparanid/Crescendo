package com.paranid5.crescendo.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.main.MainActivity

private const val NOTIFICATION_ID = 100
private const val TRIMMING_CHANNEL_ID = "trimming_status"

class TrimmingStatusReceiver : BroadcastReceiver() {
    companion object {
        private const val RECEIVER_LOCATION = "com.paranid5.crescendo.receivers"
        const val Broadcast_TRIMMING_COMPLETED = "$RECEIVER_LOCATION.TRIMMING_COMPLETED"
        const val TRIMMING_STATUS_ARG = "trimming_status_arg"
    }

    private var isNotificationChannelCreated = false
    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getStringExtra(TRIMMING_STATUS_ARG)!!
        notificationManager = context.getSystemService<NotificationManager>()!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannelOnce()

        notificationManager.showTrimmerStatusNotificationCompat(context, status)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelOnce() {
        if (isNotificationChannelCreated) return
        notificationManager.createNotificationChannel(TrimmingNotificationChannel())
        isNotificationChannelCreated = true
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun TrimmingNotificationChannel() =
    NotificationChannel(
        TRIMMING_CHANNEL_ID,
        "Trimming Status",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        enableVibration(true)
        enableLights(true)
    }

private fun NotificationManager.showTrimmerStatusNotificationCompat(
    context: Context,
    status: String
) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && areNotificationsEnabled() ->
        showTrimmerStatusNotification(context, status)

    else -> showTrimmerStatusNotification(context, status)
}

private fun NotificationManager.showTrimmerStatusNotification(
    context: Context,
    status: String
) = notify(
    NOTIFICATION_ID,
    TrimmerStatusNotificationBuilder(context, status).build()
)

private fun TrimmerStatusNotificationBuilder(context: Context, status: String) =
    NotificationBuilderCompat(context)
        .setSmallIcon(R.drawable.crescendo)
        .setContentTitle(status)
        .setOngoing(false)
        .setShowWhen(true)
        .setOnlyAlertOnce(true)
        .setContentIntent(MainActivityPendingIntent(context))

@Suppress("DEPRECATION")
private fun NotificationBuilderCompat(context: Context) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
        Notification.Builder(context, TRIMMING_CHANNEL_ID)

    else -> Notification.Builder(context)
}

private fun MainActivityPendingIntent(context: Context) =
    PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )