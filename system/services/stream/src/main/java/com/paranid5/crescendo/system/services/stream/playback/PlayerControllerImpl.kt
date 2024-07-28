package com.paranid5.crescendo.system.services.stream.playback

import androidx.annotation.OptIn
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.datastore.sources.playback.RepeatingPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.playback.RepeatingSubscriberImpl
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.PlaybackRepository.Companion.UNDEFINED_AUDIO_SESSION_ID
import com.paranid5.crescendo.domain.sources.playback.RepeatingPublisher
import com.paranid5.crescendo.domain.sources.playback.RepeatingSubscriber
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.system.services.common.playback.AudioEffectsController
import com.paranid5.system.services.common.playback.AudioEffectsControllerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TEN_SECS_AS_MILLIS = 10_000

internal class PlayerControllerImpl(
    service: StreamService,
    dataStoreProvider: DataStoreProvider,
    audioEffectsRepository: AudioEffectsRepository,
    private val playbackRepository: PlaybackRepository,
) : PlayerController,
    AudioEffectsController by AudioEffectsControllerImpl(audioEffectsRepository),
    RepeatingSubscriber by RepeatingSubscriberImpl(dataStoreProvider),
    RepeatingPublisher by RepeatingPublisherImpl(dataStoreProvider) {

    @OptIn(UnstableApi::class)
    override val player by lazy {
        ExoPlayer.Builder(service)
            .setAudioAttributes(newAudioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setPauseAtEndOfMediaItems(false)
            .build()
            .apply {
                addListener(PlayerStateChangedListener(service))
                playbackRepository.updateAudioSessionId(audioSessionId)
                initAudioEffects(audioSessionId)

                service.serviceScope.launch {
                    startRepeatMonitoring(service.lifecycle)
                }
            }
    }

    override var isPlaying
        get() = playbackRepository.isPlayingState.value
        set(value) = playbackRepository.updatePlaying(isPlaying = value)

    override val isPlayingState by lazy {
        playbackRepository.isPlayingState
    }

    private val _currentPositionState by lazy {
        MutableStateFlow(0L)
    }

    override val currentPositionState by lazy {
        _currentPositionState.asStateFlow()
    }

    private inline val currentPosition
        get() = currentPositionState.value

    override fun updateCurrentPosition() =
        _currentPositionState.update { player.currentPosition }

    override suspend fun setAndStoreRepeating(isRepeating: Boolean) {
        repeatMode = repeatMode(isRepeating)
        setRepeating(isRepeating)
    }

    override fun playStreamViaPlayer(url: String, initialPosition: Long) {
        player.setMediaItem(MediaItem.fromUri(url))
        player.playWhenReady = true
        player.prepare()
        player.seekTo(initialPosition)
    }

    override fun pausePlayer() = player.pause()

    override fun resumePlayer() {
        resetAudioSessionId()
        play()
    }

    private fun play() {
        player.playWhenReady = true
    }

    override fun resetAudioSessionIdIfNotPlaying() {
        if (!isPlaying) resetAudioSessionId()
    }

    @OptIn(UnstableApi::class)
    private fun resetAudioSessionId() =
        playbackRepository.updateAudioSessionId(player.audioSessionId)

    override fun seekToViaPlayer(position: Long) {
        resetAudioSessionId()
        player.seekTo(position)
    }

    override fun seekTenSecsBackViaPlayer() = seekToViaPlayer(
        maxOf(currentPosition - TEN_SECS_AS_MILLIS, 0)
    )

    override fun seekTenSecsForwardViaPlayer(videoDurationMillis: Long) = seekToViaPlayer(
        minOf(currentPosition + TEN_SECS_AS_MILLIS, videoDurationMillis)
    )

    private suspend inline fun startRepeatMonitoring(lifecycle: Lifecycle): Unit =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            isRepeatingFlow
                .distinctUntilChanged()
                .collectLatest { player.repeatMode = repeatMode(it) }
        }

    override fun releasePlayerWithEffects() {
        releaseAudioEffects()
        player.stop()
        player.release()
        playbackRepository.updateAudioSessionId(UNDEFINED_AUDIO_SESSION_ID)
    }
}

@OptIn(UnstableApi::class)
private inline val newAudioAttributes
    get() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()