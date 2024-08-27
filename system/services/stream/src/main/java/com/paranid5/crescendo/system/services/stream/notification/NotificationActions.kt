package com.paranid5.crescendo.system.services.stream.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.system.services.stream.Actions

internal fun RepeatActionCompat(context: Context) =
    NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context, R.drawable.ic_repeat),
        context.getString(R.string.change_repeat),
        Actions.Repeat.playbackIntent(context),
    ).build()

internal fun UnrepeatActionCompat(context: Context) =
    NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context, R.drawable.ic_no_repeat),
        context.getString(R.string.change_repeat),
        Actions.Unrepeat.playbackIntent(context),
    ).build()

internal fun DismissNotificationActionCompat(context: Context) =
    NotificationCompat.Action.Builder(
        IconCompat.createWithResource(context, R.drawable.ic_cancel),
        context.getString(R.string.cancel),
        Actions.Dismiss.playbackIntent(context),
    ).build()

private fun Actions.playbackIntent(context: Context) =
    PendingIntent.getBroadcast(
        context,
        requestCode,
        Intent(playbackAction),
        PendingIntent.FLAG_IMMUTABLE,
    )
