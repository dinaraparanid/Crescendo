package com.paranid5.crescendo.services.video_cache_service.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.services.video_cache_service.Actions

@Suppress("DEPRECATION")
fun CancelCurVideoAction(context: Context) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Notification.Action.Builder(
        null,
        context.getString(R.string.cancel),
        Actions.CancelCurVideo.playbackIntent(context)
    )

    else -> Notification.Action.Builder(
        0,
        context.getString(R.string.cancel),
        Actions.CancelCurVideo.playbackIntent(context)
    )
}.build()

@Suppress("DEPRECATION")
fun CancelAllActionCompat(context: Context) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Notification.Action.Builder(
        null,
        context.getString(R.string.cancel_all),
        Actions.CancelAll.playbackIntent(context)
    )

    else -> Notification.Action.Builder(
        0,
        context.getString(R.string.cancel_all),
        Actions.CancelAll.playbackIntent(context)
    )
}.build()

private fun Actions.playbackIntent(context: Context) =
    PendingIntent.getBroadcast(
        context,
        requestCode,
        Intent(playbackAction),
        PendingIntent.FLAG_IMMUTABLE
    )