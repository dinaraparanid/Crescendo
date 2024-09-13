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
        callback: MediaSession.Callback? = null,
    ) {
        mediaSession = MediaSession(context = context, player = player, callback = callback)
    }

    fun releaseMediaSession() = mediaSession.release()
}