package com.paranid5.system.services.common.media_session

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession

class MediaSessionManager {
    lateinit var mediaSession: MediaSession
        private set

    fun initMediaSession(
        context: Context,
        player: Player,
        mediaSessionId: String,
        callback: MediaSession.Callback,
        initialActions: List<CommandButton> = listOf(),
    ) {
        mediaSession = MediaSession(
            context = context,
            player = player,
            mediaSessionId = mediaSessionId,
            callback = callback,
            initialActions = initialActions
        )
    }

    fun releaseMediaSession() = mediaSession.release()
}