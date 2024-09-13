package com.paranid5.system.services.common.media_session

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaSession

internal fun MediaSession(
    context: Context,
    player: Player,
    callback: MediaSession.Callback?,
) = MediaSession
    .Builder(context, player)
    .run { callback?.let(this::setCallback) ?: this }
    .build()
