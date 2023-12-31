package com.paranid5.crescendo.services.stream_service.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.paranid5.crescendo.R
import com.paranid5.crescendo.services.stream_service.Actions

fun RepeatActionCompat(context: Context) =
    NotificationCompat.Action.Builder(
        IconCompat.createWithResource(
            context,
            R.drawable.repeat
        ),
        context.getString(R.string.change_repeat),
        Actions.Repeat.playbackIntent(context)
    ).build()

fun UnrepeatActionCompat(context: Context) =
    NotificationCompat.Action.Builder(
        IconCompat.createWithResource(
            context,
            R.drawable.no_repeat
        ),
        context.getString(R.string.change_repeat),
        Actions.Unrepeat.playbackIntent(context)
    ).build()

fun DismissNotificationActionCompat(context: Context) =
    NotificationCompat.Action.Builder(
        IconCompat.createWithResource(
            context,
            R.drawable.dismiss
        ),
        context.getString(R.string.cancel),
        Actions.Dismiss.playbackIntent(context)
    ).build()

private fun Actions.playbackIntent(context: Context) =
    PendingIntent.getBroadcast(
        context,
        requestCode,
        Intent(playbackAction),
        PendingIntent.FLAG_IMMUTABLE
    )