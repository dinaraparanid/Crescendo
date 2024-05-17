package com.paranid5.crescendo.system.services.video_cache.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.system.common.intent.mainActivityIntent

internal fun DownloadNotificationBuilder(
    context: Context,
    videoTitle: String,
    videoQueueLen: Int,
    downloadedBytes: Long,
    totalBytes: Long
) = BusyNotificationBuilder(context, "${context.getString(R.string.downloading)}: $videoTitle")
    .setContentText("${context.getString(R.string.tracks_in_queue)}: $videoQueueLen")
    .setProgress(totalBytes.toInt(), downloadedBytes.toInt(), false)
    .addAction(CancelCurVideoAction(context))
    .addAction(CancelAllActionCompat(context))

internal fun ConvertingNotificationBuilder(context: Context, videoTitle: String) =
    BusyNotificationBuilder(context, "${context.getString(R.string.converting)}: $videoTitle")

private fun BusyNotificationBuilder(context: Context, title: String) =
    NotificationBuilder(context)
        .setContentTitle(title)
        .setOngoing(true)
        .setShowWhen(false)
        .setOnlyAlertOnce(true)

internal fun CanceledNotificationBuilder(context: Context) =
    MessageNotificationBuilder(context, context.getString(R.string.video_canceled))

internal fun DownloadErrorNotificationBuilder(context: Context, code: Int, description: String) =
    MessageNotificationBuilder(context, "${context.getString(R.string.error)} $code: $description")

internal fun ConnectionLostNotificationBuilder(context: Context) =
    MessageNotificationBuilder(context, context.getString(R.string.connection_lost))

internal fun CachedNotificationBuilder(context: Context) =
    MessageNotificationBuilder(context, context.getString(R.string.video_cached))

private fun MessageNotificationBuilder(context: Context, message: String) =
    NotificationBuilder(context)
        .setContentTitle(message)
        .setAutoCancel(true)
        .setShowWhen(false)

private fun NotificationBuilder(context: Context) =
    NotificationBuilderCompat(context)
        .setSmallIcon(R.drawable.save)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                context.mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

@Suppress("DEPRECATION")
private fun NotificationBuilderCompat(context: Context) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
        Notification.Builder(context, VIDEO_CACHE_CHANNEL_ID)

    else -> Notification.Builder(context)
}