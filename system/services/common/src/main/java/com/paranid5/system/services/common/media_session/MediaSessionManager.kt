package com.paranid5.system.services.common.media_session

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaSession

class MediaSessionManager {
    lateinit var mediaSession: MediaSession
        private set

    fun initMediaSession(
        context: Context,
        player: Player,
        mediaSessionId: String,
        callback: MediaSession.Callback,
    ) {
        mediaSession = MediaSession(
            context = context,
            player = player,
            mediaSessionId = mediaSessionId,
            callback = callback,
        )
    }

    fun releaseMediaSession() = mediaSession.release()
}