package com.paranid5.crescendo.system.services.stream.media_session

import android.content.Context
import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.system.services.stream.ACTION_DISMISS
import com.paranid5.crescendo.system.services.stream.ACTION_REPEAT
import com.paranid5.crescendo.system.services.stream.ACTION_UNREPEAT

internal fun RepeatAction(context: Context, isRepeating: Boolean) = when {
    isRepeating -> CommandButton.Builder()
        .setDisplayName(context.getString(R.string.change_repeat))
        .setSessionCommand(SessionCommand(ACTION_REPEAT, Bundle.EMPTY))
        .setIconResId(R.drawable.ic_repeat)

    else -> CommandButton.Builder()
        .setDisplayName(context.getString(R.string.change_repeat))
        .setSessionCommand(SessionCommand(ACTION_UNREPEAT, Bundle.EMPTY))
        .setIconResId(R.drawable.ic_no_repeat)
}.build()

internal fun CancelAction(context: Context) =
    CommandButton.Builder()
        .setDisplayName(context.getString(R.string.cancel))
        .setSessionCommand(SessionCommand(ACTION_DISMISS, Bundle.EMPTY))
        .setIconResId(R.drawable.ic_cancel)
        .build()