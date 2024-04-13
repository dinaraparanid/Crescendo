package com.paranid5.crescendo.system.services.stream.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionPublisherImpl
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionSubscriberImpl
import com.paranid5.crescendo.data.sources.stream.CurrentMetadataSubscriberImpl
import com.paranid5.crescendo.data.sources.stream.CurrentUrlPublisherImpl
import com.paranid5.crescendo.data.sources.stream.CurrentUrlSubscriberImpl
import com.paranid5.crescendo.domain.sources.playback.StreamPlaybackPositionPublisher
import com.paranid5.crescendo.domain.sources.playback.StreamPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.sources.stream.CurrentMetadataSubscriber
import com.paranid5.crescendo.domain.sources.stream.CurrentUrlPublisher
import com.paranid5.crescendo.domain.sources.stream.CurrentUrlSubscriber
import com.paranid5.crescendo.system.services.stream.StreamService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinComponent

@Suppress("IncorrectFormatting")
internal class PlayerProvider(service: StreamService, storageRepository: StorageRepository) :
    KoinComponent,
    PlayerController by PlayerControllerImpl(service, storageRepository),
    CurrentUrlSubscriber by CurrentUrlSubscriberImpl(storageRepository),
    CurrentUrlPublisher by CurrentUrlPublisherImpl(storageRepository),
    CurrentMetadataSubscriber by CurrentMetadataSubscriberImpl(storageRepository),
    StreamPlaybackPositionSubscriber by StreamPlaybackPositionSubscriberImpl(storageRepository),
    StreamPlaybackPositionPublisher by StreamPlaybackPositionPublisherImpl(storageRepository) {
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