package com.paranid5.crescendo.services.stream_service.media_session

import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStatePublisher
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStatePublisherImpl
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriberImpl
import com.paranid5.crescendo.services.stream_service.StreamService2

private const val TAG = "MediaSessionManager"

class MediaSessionManager(storageHandler: StorageHandler) :
    CurrentMetadataStateSubscriber by CurrentMetadataStateSubscriberImpl(storageHandler),
    CurrentMetadataStatePublisher by CurrentMetadataStatePublisherImpl(storageHandler) {
    private lateinit var mediaSession: MediaSessionCompat

    val sessionToken: MediaSessionCompat.Token
        get() = mediaSession.sessionToken

    fun initMediaSession(
        service: StreamService2,
        mediaSessionCallback: MediaSessionCompat.Callback,
    ) {
        mediaSession = MediaSession(service, mediaSessionCallback)
    }

    fun updatePlaybackState(state: PlaybackStateCompat) =
        mediaSession.setPlaybackState(state)

    fun updateMetadata(metadata: MediaMetadataCompat) =
        mediaSession.setMetadata(metadata)

    fun releaseMediaSession() = mediaSession.release()
}

private fun MediaSession(
    service: StreamService2,
    mediaSessionCallback: MediaSessionCompat.Callback,
) = MediaSessionCompat(service.applicationContext, TAG).apply {
    isActive = true

    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) setFlags(
        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
    )

    setCallback(mediaSessionCallback)
}