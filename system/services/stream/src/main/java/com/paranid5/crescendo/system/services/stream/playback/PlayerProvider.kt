package com.paranid5.crescendo.system.services.stream.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionStatePublisher
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionStatePublisherImpl
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionStateSubscriber
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionStateSubscriberImpl
import com.paranid5.crescendo.data.sources.stream.CurrentMetadataStateSubscriber
import com.paranid5.crescendo.data.sources.stream.CurrentMetadataStateSubscriberImpl
import com.paranid5.crescendo.data.sources.stream.CurrentUrlStatePublisher
import com.paranid5.crescendo.data.sources.stream.CurrentUrlStatePublisherImpl
import com.paranid5.crescendo.data.sources.stream.CurrentUrlStateSubscriber
import com.paranid5.crescendo.data.sources.stream.CurrentUrlStateSubscriberImpl
import com.paranid5.crescendo.system.services.stream.StreamService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinComponent

@Suppress("IncorrectFormatting")
internal class PlayerProvider(service: StreamService, storageRepository: StorageRepository) :
    KoinComponent,
    PlayerController by PlayerControllerImpl(service, storageRepository),
    CurrentUrlStateSubscriber by CurrentUrlStateSubscriberImpl(storageRepository),
    CurrentUrlStatePublisher by CurrentUrlStatePublisherImpl(storageRepository),
    CurrentMetadataStateSubscriber by CurrentMetadataStateSubscriberImpl(storageRepository),
    StreamPlaybackPositionStateSubscriber by StreamPlaybackPositionStateSubscriberImpl(storageRepository),
    StreamPlaybackPositionStatePublisher by StreamPlaybackPositionStatePublisherImpl(storageRepository) {
    private val _playbackEventFlow by lazy {
        MutableSharedFlow<PlaybackEvent>()
    }

    val playbackEventFlow by lazy {
        _playbackEventFlow.asSharedFlow()
    }

    @Volatile
    var isStoppedWithError = false

    inline val currentPosition
        get() = currentPositionState.value

    suspend fun storePlaybackPosition() =
        setStreamPlaybackPosition(currentPosition)

    var playbackParameters
        @MainThread get() = player.playbackParameters
        @MainThread set(value) {
            player.playbackParameters = value
        }

    suspend fun startStream(url: String, initialPosition: Long = 0) =
        _playbackEventFlow.emit(PlaybackEvent.StartNewStream(url, initialPosition))

    suspend fun startResuming() =
        _playbackEventFlow.emit(PlaybackEvent.StartSameStream())

    suspend fun resume() =
        _playbackEventFlow.emit(PlaybackEvent.Resume())

    suspend fun pause() =
        _playbackEventFlow.emit(PlaybackEvent.Pause())

    suspend fun seekTo(position: Long) =
        _playbackEventFlow.emit(PlaybackEvent.SeekTo(position))

    suspend fun seekTenSecsForward() =
        _playbackEventFlow.emit(PlaybackEvent.SeekTenSecsForward())

    suspend fun seekTenSecsBack() =
        _playbackEventFlow.emit(PlaybackEvent.SeekTenSecsBack())

    suspend fun restartPlayer() = startResuming()
}