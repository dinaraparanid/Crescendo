package com.paranid5.system.services.common.media_session

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession

@OptIn(UnstableApi::class)
internal fun MediaSession(
    context: Context,
    player: Player,
    mediaSessionId: String,
    callback: MediaSession.Callback,
) = MediaSession
    .Builder(context, player)
    .setId(mediaSessionId)
    .setCallback(callback)
    .build()
