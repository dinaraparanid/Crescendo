package com.paranid5.system.services.common.media_session

import android.content.Context
import android.media.session.MediaSession
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class MediaSessionManager {
    lateinit var mediaSession: MediaSessionCompat
        private set

    val sessionToken: MediaSession.Token
        get() = mediaSession.sessionToken.token as MediaSession.Token

    fun initMediaSession(
        context: Context,
        mediaSessionCallback: MediaSessionCompat.Callback,
    ) {
        mediaSession = MediaSession(context, mediaSessionCallback)
    }

    fun updatePlaybackState(state: PlaybackStateCompat) =
        mediaSession.setPlaybackState(state)

    fun updateMetadata(metadata: MediaMetadataCompat) =
        mediaSession.setMetadata(metadata)

    fun releaseMediaSession() = mediaSession.release()
}