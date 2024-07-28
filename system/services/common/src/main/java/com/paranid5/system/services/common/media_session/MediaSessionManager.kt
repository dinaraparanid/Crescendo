package com.paranid5.system.services.common.media_session

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.datastore.sources.stream.CurrentMetadataPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.stream.CurrentMetadataSubscriberImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.CurrentTrackSubscriberImpl
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.stream.CurrentMetadataPublisher
import com.paranid5.crescendo.domain.sources.stream.CurrentMetadataSubscriber
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackSubscriber

class MediaSessionManager(
    dataStoreProvider: DataStoreProvider,
    currentPlaylistRepository: CurrentPlaylistRepository
) : CurrentMetadataSubscriber by CurrentMetadataSubscriberImpl(dataStoreProvider),
    CurrentMetadataPublisher by CurrentMetadataPublisherImpl(dataStoreProvider),
    CurrentTrackSubscriber by CurrentTrackSubscriberImpl(
        dataStoreProvider,
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