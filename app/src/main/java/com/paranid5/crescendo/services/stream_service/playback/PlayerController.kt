package com.paranid5.crescendo.services.stream_service.playback

import androidx.annotation.MainThread
import androidx.annotation.OptIn
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.paranid5.crescendo.AUDIO_SESSION_ID
import com.paranid5.crescendo.IS_PLAYING
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.playback.RepeatingStatePublisher
import com.paranid5.crescendo.data.states.playback.RepeatingStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriber
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriberImpl
import com.paranid5.crescendo.services.stream_service.StreamService2
import com.paranid5.crescendo.services.stream_service.playback.effects.AudioEffectsController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.atomic.AtomicLong

private const val TEN_SECS_AS_MILLIS = 10000

class PlayerController(service: StreamService2, storageHandler: StorageHandler) : KoinComponent,
    RepeatingStateSubscriber by RepeatingStateSubscriberImpl(storageHandler),
    RepeatingStatePublisher by RepeatingStatePublisherImpl(storageHandler) {
    private val _isPlayingState by inject<MutableStateFlow<Boolean>>(named(IS_PLAYING))

    private val audioSessionIdState by inject<MutableStateFlow<Int>>(named(AUDIO_SESSION_ID))

    internal val audioEffectsController by lazy {
        AudioEffectsController(storageHandler)
    }

    @OptIn(UnstableApi::class)
    internal val player by lazy {
        ExoPlayer.Builder(service)
            .setAudioAttributes(newAudioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setPauseAtEndOfMediaItems(false)
            .build()
            .apply {
                addListener(PlayerStateChangedListener(service))
                audioSessionIdState.update { audioSessionId }
                audioEffectsController.initAudioEffects(audioSessionId)

                service.serviceScope.launch {
                    startRepeatMonitoring(service.lifecycle)
                }
            }
    }

    var isPlaying
        get() = _isPlayingState.value
        set(value) = _isPlayingState.update { value }

    val isPlayingState by lazy {
        _isPlayingState.asStateFlow()
    }

    val currentPosition = AtomicLong()

    @MainThread
    fun updateCurrentPosition() =
        currentPosition.set(player.currentPosition)

    suspend fun setAndStoreRepeating(isRepeating: Boolean) {
        player.repeatMode = getRepeatMode(isRepeating)
        setRepeating(isRepeating)
    }

    fun playStream(url: String, initialPosition: Long) {
        player.setMediaItem(MediaItem.fromUri(url))
        player.playWhenReady = true
        player.prepare()
        player.seekTo(initialPosition)
    }

    fun pause() = player.pause()

    fun resume() {
        resetAudioSessionId()
        play()
    }

    private fun play() {
        player.playWhenReady = true
    }

    fun resetAudioSessionIdIfNotPlaying() {
        if (!isPlaying) resetAudioSessionId()
    }

    @OptIn(UnstableApi::class)
    private fun resetAudioSessionId() =
        audioSessionIdState.update { player.audioSessionId }

    fun seekTo(position: Long) {
        resetAudioSessionId()
        player.seekTo(position)
    }

    fun seekTenSecsBack() = seekTo(
        maxOf(currentPosition.get() - TEN_SECS_AS_MILLIS, 0)
    )

    fun seekTenSecsForward(videoDurationMillis: Long) = seekTo(
        minOf(currentPosition.get() + TEN_SECS_AS_MILLIS, videoDurationMillis)
    )

    private suspend inline fun startRepeatMonitoring(lifecycle: Lifecycle): Unit =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            isRepeatingFlow
                .distinctUntilChanged()
                .collectLatest { player.repeatMode = getRepeatMode(it) }
        }

    fun releasePlayerWithEffects() {
        audioEffectsController.releaseAudioEffects()
        player.stop()
        player.release()
        audioSessionIdState.update { 0 }
    }
}

@OptIn(UnstableApi::class)
private inline val newAudioAttributes
    get() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()

private fun getRepeatMode(isRepeating: Boolean) = when {
    isRepeating -> Player.REPEAT_MODE_ONE
    else -> Player.REPEAT_MODE_OFF
}