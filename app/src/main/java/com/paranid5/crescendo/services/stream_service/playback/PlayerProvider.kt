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
import com.paranid5.crescendo.domain.utils.AsyncCondVar
import com.paranid5.crescendo.services.stream_service.StreamService2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinComponent
import java.util.concurrent.atomic.AtomicInteger

private const val PLAYBACK_EVENT_LOOP_INIT_STEPS = 2

@Suppress("IncorrectFormatting")
class PlayerProvider(service: StreamService2, storageHandler: StorageHandler) : KoinComponent,
    PlayerController by PlayerControllerImpl(service, storageHandler),
    CurrentUrlStateSubscriber by CurrentUrlStateSubscriberImpl(storageHandler),
    CurrentUrlStatePublisher by CurrentUrlStatePublisherImpl(storageHandler),
    CurrentMetadataStateSubscriber by CurrentMetadataStateSubscriberImpl(storageHandler),
    StreamPlaybackPositionStateSubscriber by StreamPlaybackPositionStateSubscriberImpl(storageHandler),
    StreamPlaybackPositionStatePublisher by StreamPlaybackPositionStatePublisherImpl(storageHandler) {
    private lateinit var playbackEventFlow: MutableSharedFlow<PlaybackEvent>

    private val eventFlowInitSteps = AtomicInteger()

    private val eventFlowInitCondVar = AsyncCondVar()

    internal suspend inline fun incrementPlaybackEventLoopInitSteps() {
        if (eventFlowInitSteps.incrementAndGet() == PLAYBACK_EVENT_LOOP_INIT_STEPS)
            eventFlowInitCondVar.notify()
    }

    private suspend inline fun waitEventFlowInit() {
        while (eventFlowInitSteps.get() != PLAYBACK_EVENT_LOOP_INIT_STEPS)
            eventFlowInitCondVar.wait()
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

    suspend fun startPlaybackEventLoop(service: StreamService2) {
        playbackEventFlow = PlaybackEventLoop(service)
        incrementPlaybackEventLoopInitSteps()
    }

    suspend fun storeAndPlayStream(url: String, initialPosition: Long = 0) {
        waitEventFlowInit()
        setCurrentUrl(url)
        setStreamPlaybackPosition(initialPosition)
        playbackEventFlow.emit(PlaybackEvent.StartNewStream(url, initialPosition))
    }

    suspend fun startResuming() {
        waitEventFlowInit()
        delay(500L)
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
}