package com.paranid5.crescendo.services.stream_service.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.effects.SpeedStateSubscriber
import com.paranid5.crescendo.data.states.effects.SpeedStateSubscriberImpl
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStatePublisher
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStateSubscriber
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStateSubscriberImpl
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriberImpl
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriberImpl
import com.paranid5.crescendo.services.stream_service.StreamService2
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinComponent

@Suppress("IncorrectFormatting")
class PlayerProvider(service: StreamService2, storageHandler: StorageHandler) : KoinComponent,
    CurrentUrlStateSubscriber by CurrentUrlStateSubscriberImpl(storageHandler),
    CurrentMetadataStateSubscriber by CurrentMetadataStateSubscriberImpl(storageHandler),
    StreamPlaybackPositionStateSubscriber by StreamPlaybackPositionStateSubscriberImpl(storageHandler),
    StreamPlaybackPositionStatePublisher by StreamPlaybackPositionStatePublisherImpl(storageHandler),
    SpeedStateSubscriber by SpeedStateSubscriberImpl(storageHandler) {
    private lateinit var playbackEventFlow: MutableSharedFlow<PlaybackEvent>

    internal val playerController by lazy {
        PlayerController(service, storageHandler)
    }

    internal val player
        get() = playerController.player

    @Volatile
    var isStoppedWithError = false

    var isPlaying
        get() = playerController.isPlaying
        set(value) {
            playerController.isPlaying = value
        }

    val isPlayingState by lazy {
        playerController.isPlayingState
    }

    val isRepeatingFlow by lazy {
        playerController.isRepeatingFlow
    }

    var currentPosition
        get() = playerController.currentPosition.get()
        set(value) = playerController.currentPosition.set(value)

    fun updateCurrentPosition() =
        playerController.updateCurrentPosition()

    suspend fun storePlaybackPosition() =
        setStreamPlaybackPosition(player.currentPosition)

    var playbackParameters
        @MainThread get() = player.playbackParameters
        @MainThread set(value) {
            player.playbackParameters = value
        }

    suspend fun startPlaybackEventLoop(service: StreamService2) {
        playbackEventFlow = PlaybackEventLoop(service)
    }

    suspend fun storeAndPlayStream(url: String, initialPosition: Long = 0) {
        setStreamPlaybackPosition(initialPosition)
        playbackEventFlow.emit(PlaybackEvent.StartNewStream(url, initialPosition))
    }

    suspend fun startResuming() =
        playbackEventFlow.emit(PlaybackEvent.StartSameStream)

    suspend fun resume() =
        playbackEventFlow.emit(PlaybackEvent.Resume)

    suspend fun pause() =
        playbackEventFlow.emit(PlaybackEvent.Pause)

    suspend fun seekTo(position: Long) =
        playbackEventFlow.emit(PlaybackEvent.SeekTo(position))

    suspend fun seekTenSecsForward() =
        playbackEventFlow.emit(PlaybackEvent.SeekTenSecsForward)

    suspend fun seekTenSecsBack() =
        playbackEventFlow.emit(PlaybackEvent.SeekTenSecsBack)

    suspend fun restartPlayer() = startResuming()

    fun resetAudioSessionIdIfNotPlaying() =
        playerController.resetAudioSessionIdIfNotPlaying()

    fun releasePlayerWithEffects() =
        playerController.releasePlayerWithEffects()
}