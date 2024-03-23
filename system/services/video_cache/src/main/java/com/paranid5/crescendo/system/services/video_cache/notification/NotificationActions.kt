package com.paranid5.crescendo.system.services.video_cache.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.system.services.video_cache.Actions

@Suppress("DEPRECATION")
internal fun CancelCurVideoAction(context: Context) = when {
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
internal fun CancelAllActionCompat(context: Context) = when {
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