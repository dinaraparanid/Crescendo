package com.paranid5.crescendo.services.service_controllers

import android.content.Context
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class MediaSessionController(context: Context, tag: String) {
    val mediaSession by lazy {
        MediaSessionCompat(context.applicationContext, tag)
    }

    val transportControls: MediaControllerCompat.TransportControls by lazy {
        mediaSession.controller.transportControls
    }

    fun initMediaSession(
        mediaSessionCallback: MediaSessionCompat.Callback,
        playbackState: PlaybackStateCompat
    ) {
        mediaSession.isActive = true

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                    or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
        )

        mediaSession.setCallback(mediaSessionCallback)
        mediaSession.setPlaybackState(playbackState)
    }

    fun updateMediaSession(
        playbackState: PlaybackStateCompat,
        metadata: MediaMetadataCompat?
    ) = mediaSession.run {
        setPlaybackState(playbackState)
        setMetadata(metadata)
    }

    fun releaseMediaSession() {
        mediaSession.release()
        transportControls.stop()
    }
}