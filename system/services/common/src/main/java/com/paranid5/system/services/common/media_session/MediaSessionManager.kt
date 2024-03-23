package com.paranid5.system.services.common.media_session

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStatePublisher
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStatePublisherImpl
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriberImpl
import com.paranid5.crescendo.data.states.tracks.CurrentTrackStateSubscriber
import com.paranid5.crescendo.data.states.tracks.CurrentTrackStateSubscriberImpl

class MediaSessionManager(
    storageRepository: StorageRepository,
    currentPlaylistRepository: CurrentPlaylistRepository
) : CurrentMetadataStateSubscriber by CurrentMetadataStateSubscriberImpl(storageRepository),
    CurrentMetadataStatePublisher by CurrentMetadataStatePublisherImpl(storageRepository),
    CurrentTrackStateSubscriber by CurrentTrackStateSubscriberImpl(
        storageRepository,
        currentPlaylistRepository
    ) {
    private lateinit var mediaSession: MediaSessionCompat

    val sessionToken: MediaSessionCompat.Token
        get() = mediaSession.sessionToken

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