package com.paranid5.crescendo.services.stream_service.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStatePublisher
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStateSubscriber
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStateSubscriberImpl
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriberImpl
import com.paranid5.crescendo.data.states.stream.CurrentUrlStatePublisher
import com.paranid5.crescendo.data.states.stream.CurrentUrlStatePublisherImpl
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriberImpl
import com.paranid5.crescendo.services.stream_service.StreamService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinComponent

@Suppress("IncorrectFormatting")
class PlayerProvider(service: StreamService, storageHandler: StorageHandler) : KoinComponent,
    PlayerController by PlayerControllerImpl(service, storageHandler),
    CurrentUrlStateSubscriber by CurrentUrlStateSubscriberImpl(storageHandler),
    CurrentUrlStatePublisher by CurrentUrlStatePublisherImpl(storageHandler),
    CurrentMetadataStateSubscriber by CurrentMetadataStateSubscriberImpl(storageHandler),
    StreamPlaybackPositionStateSubscriber by StreamPlaybackPositionStateSubscriberImpl(storageHandler),
    StreamPlaybackPositionStatePublisher by StreamPlaybackPositionStatePublisherImpl(storageHandler) {
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