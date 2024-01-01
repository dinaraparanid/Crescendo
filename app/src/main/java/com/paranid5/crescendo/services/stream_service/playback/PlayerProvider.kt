package com.paranid5.crescendo.services.stream_service.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.effects.SpeedStateSubscriber
import com.paranid5.crescendo.data.states.effects.SpeedStateSubscriberImpl
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriber
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriberImpl
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
import com.paranid5.crescendo.domain.utils.AsyncCondVar
import com.paranid5.crescendo.services.stream_service.StreamService2
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinComponent

@Suppress("IncorrectFormatting")
class PlayerProvider(service: StreamService2, storageHandler: StorageHandler) : KoinComponent,
    CurrentUrlStateSubscriber by CurrentUrlStateSubscriberImpl(storageHandler),
    CurrentUrlStatePublisher by CurrentUrlStatePublisherImpl(storageHandler),
    CurrentMetadataStateSubscriber by CurrentMetadataStateSubscriberImpl(storageHandler),
    StreamPlaybackPositionStateSubscriber by StreamPlaybackPositionStateSubscriberImpl(storageHandler),
    StreamPlaybackPositionStatePublisher by StreamPlaybackPositionStatePublisherImpl(storageHandler),
    RepeatingStateSubscriber by RepeatingStateSubscriberImpl(storageHandler),
    SpeedStateSubscriber by SpeedStateSubscriberImpl(storageHandler) {
    private lateinit var playbackEventFlow: MutableSharedFlow<PlaybackEvent>

    @Volatile
    private var isEventFlowInitialized = false

    private val eventFlowInitCondVar = AsyncCondVar()

    private suspend inline fun markEventFlowInitialized() {
        isEventFlowInitialized = true
        eventFlowInitCondVar.notify()
    }

    private suspend inline fun waitEventFlowInit() {
        while (!isEventFlowInitialized)
            eventFlowInitCondVar.wait()
    }

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

    var currentPosition
        get() = playerController.currentPosition.get()
        set(value) = playerController.currentPosition.set(value)

    fun updateCurrentPosition() =
        playerController.updateCurrentPosition()

    suspend fun storePlaybackPosition() =
        setStreamPlaybackPosition(currentPosition)

    var playbackParameters
        @MainThread get() = player.playbackParameters
        @MainThread set(value) {
            player.playbackParameters = value
        }

    suspend fun startPlaybackEventLoop(service: StreamService2) {
        playbackEventFlow = PlaybackEventLoop(service)
        markEventFlowInitialized()
    }

    suspend fun storeAndPlayStream(url: String, initialPosition: Long = 0) {
        waitEventFlowInit()
        setCurrentUrl(url)
        setStreamPlaybackPosition(initialPosition)
        playbackEventFlow.emit(PlaybackEvent.StartNewStream(url, initialPosition))
    }

    suspend fun startResuming() {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.StartSameStream())
    }

    suspend fun resume() {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.Resume())
    }

    suspend fun pause() {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.Pause())
    }

    suspend fun seekTo(position: Long) {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.SeekTo(position))
    }

    suspend fun seekTenSecsForward() {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.SeekTenSecsForward())
    }

    suspend fun seekTenSecsBack() {
        waitEventFlowInit()
        playbackEventFlow.emit(PlaybackEvent.SeekTenSecsBack())
    }

    suspend fun restartPlayer() = startResuming()

    fun resetAudioSessionIdIfNotPlaying() =
        playerController.resetAudioSessionIdIfNotPlaying()

    fun releasePlayerWithEffects() =
        playerController.releasePlayerWithEffects()

    internal fun playStreamImpl(url: String, initialPosition: Long) =
        playerController.playStream(url, initialPosition)
}