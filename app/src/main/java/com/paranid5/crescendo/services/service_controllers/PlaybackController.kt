package com.paranid5.crescendo.services.service_controllers

import android.content.Context
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.paranid5.crescendo.AUDIO_SESSION_ID
import com.paranid5.crescendo.EQUALIZER_DATA
import com.paranid5.crescendo.domain.eq.EqualizerData
import com.paranid5.crescendo.domain.eq.EqualizerParameters
import com.paranid5.crescendo.domain.utils.extensions.setParameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class PlaybackController(
    context: Context,
    private val playbackType: PlaybackType,
    playerStateChangedListener: Player.Listener,
    mediaRetrieverController: MediaRetrieverController,
) : KoinComponent {
    companion object {
        private const val TEN_SECS_AS_MILLIS = 10000
    }

    enum class PlaybackType { STREAM, TRACKS }

    private val audioSessionIdState by inject<MutableStateFlow<Int>>(
        named(AUDIO_SESSION_ID)
    )

    private val equalizerDataState by inject<MutableStateFlow<EqualizerData?>>(
        named(EQUALIZER_DATA)
    )

    private lateinit var equalizer: Equalizer

    lateinit var bassBoost: BassBoost
        private set

    lateinit var reverb: PresetReverb
        private set

    @OptIn(UnstableApi::class)
    val player by lazy {
        ExoPlayer.Builder(context)
            .setAudioAttributes(newAudioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setPauseAtEndOfMediaItems(false)
            .build()
            .apply {
                addListener(playerStateChangedListener)
                repeatMode = getRepeatMode(isRepeating = mediaRetrieverController.isRepeating)

                initAudioEffects(
                    audioSessionId = audioSessionIdState.updateAndGet { audioSessionId },
                    mediaRetrieverController = mediaRetrieverController
                )

                if (mediaRetrieverController.areAudioEffectsEnabled)
                    setAudioEffectsEnabled(
                        isEnabled = true,
                        mediaRetrieverController = mediaRetrieverController,
                        exoPlayer = this
                    )
            }
    }

    @OptIn(UnstableApi::class)
    private inline val newAudioAttributes
        get() = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(C.USAGE_MEDIA)
            .build()

    inline val isPlaying
        get() = player.isPlaying

    inline val currentPosition
        get() = player.currentPosition

    inline val currentMediaItemIndex
        get() = player.currentMediaItemIndex

    fun pause() = player.pause()

    private fun play() {
        player.playWhenReady = true
    }

    inline var playbackParameters
        get() = player.playbackParameters
        set(value) {
            player.playbackParameters = value
        }

    inline var repeatMode
        get() = player.repeatMode
        set(value) {
            player.repeatMode = value
        }

    fun releasePlayer() {
        player.stop()
        player.release()
        audioSessionIdState.update { 0 }
    }

    @OptIn(UnstableApi::class)
    private fun resetAudioSessionId() = audioSessionIdState.update { player.audioSessionId }

    fun resetAudioSessionIdIfNotPlaying() {
        if (!isPlaying) resetAudioSessionId()
    }

    private fun EqualizerWithData(
        audioSessionId: Int,
        bandLevels: List<Short>?,
        currentPreset: Short,
        currentParameter: EqualizerParameters
    ): Pair<Equalizer, EqualizerData> {
        val eq = Equalizer(0, audioSessionId)

        val data = EqualizerData(
            eq = eq,
            bandLevels = bandLevels,
            currentPreset = currentPreset,
            currentParameter = currentParameter
        )

        eq.setParameter(
            currentParameter = data.currentParameter,
            bandLevels = data.bandLevels,
            preset = data.currentPreset
        )

        return eq to data
    }

    private fun BassBoost(audioSessionId: Int, bassStrength: Short) =
        BassBoost(0, audioSessionId).apply {
            try {
                setStrength(bassStrength)
            } catch (ignored: IllegalArgumentException) {
                // Invalid strength
            }
        }

    private fun Reverb(audioSessionId: Int, reverbPreset: Short) =
        PresetReverb(0, audioSessionId).apply {
            try {
                preset = reverbPreset
            } catch (ignored: IllegalArgumentException) {
                // Invalid preset
            }
        }

    private fun initAudioEffects(
        audioSessionId: Int,
        mediaRetrieverController: MediaRetrieverController
    ) {
        initEqualizerCatching(audioSessionId, mediaRetrieverController)
        initBassBoostCatching(audioSessionId, mediaRetrieverController)
        initReverbCatching(audioSessionId, mediaRetrieverController)
    }

    private fun initEqualizer(
        audioSessionId: Int,
        mediaRetrieverController: MediaRetrieverController
    ) {
        val (eq, eqData) = EqualizerWithData(
            audioSessionId = audioSessionId,
            bandLevels = mediaRetrieverController.equalizerBands,
            currentPreset = mediaRetrieverController.equalizerPreset,
            currentParameter = mediaRetrieverController.equalizerParams
        )

        equalizer = eq
        equalizerDataState.updateAndGet { eqData }
    }

    private fun initEqualizerCatching(
        audioSessionId: Int,
        mediaRetrieverController: MediaRetrieverController
    ) = runCatching {
        initEqualizer(audioSessionId, mediaRetrieverController)
    }

    private fun initBassBoost(
        audioSessionId: Int,
        mediaRetrieverController: MediaRetrieverController
    ) {
        bassBoost = BassBoost(
            audioSessionId = audioSessionId,
            bassStrength = mediaRetrieverController.bassStrength
        )
    }

    private fun initBassBoostCatching(
        audioSessionId: Int,
        mediaRetrieverController: MediaRetrieverController
    ) = runCatching {
        initBassBoost(audioSessionId, mediaRetrieverController)
    }

    private fun initReverb(
        audioSessionId: Int,
        mediaRetrieverController: MediaRetrieverController
    ) {
        reverb = Reverb(
            audioSessionId = audioSessionId,
            reverbPreset = mediaRetrieverController.reverbPreset
        )
    }

    private fun initReverbCatching(
        audioSessionId: Int,
        mediaRetrieverController: MediaRetrieverController
    ) = runCatching {
        initReverb(audioSessionId, mediaRetrieverController)
    }

    fun setAudioEffectsEnabled(
        isEnabled: Boolean,
        mediaRetrieverController: MediaRetrieverController,
        exoPlayer: Player = player
    ) {
        exoPlayer.playbackParameters = when {
            isEnabled -> PlaybackParameters(
                mediaRetrieverController.speed,
                mediaRetrieverController.pitch
            )

            else -> PlaybackParameters(1F, 1F)
        }

        // For some reason, it requires multiple tries to enable...
        repeat(3) {
            try {
                equalizer.enabled = isEnabled
                bassBoost.enabled = isEnabled
                reverb.enabled = isEnabled
            } catch (ignored: IllegalStateException) {
                // not initialized
            }
        }
    }

    fun setEqParameter(
        currentParameter: EqualizerParameters,
        bandLevels: List<Short>?,
        preset: Short
    ) = equalizer.setParameter(currentParameter, bandLevels, preset)

    fun updateEqData(
        bandLevels: List<Short>?,
        currentPreset: Short,
        currentParameter: EqualizerParameters
    ) = equalizerDataState.update {
        EqualizerData(
            equalizer,
            bandLevels,
            currentPreset,
            currentParameter
        )
    }

    inline var bassStrength
        get() = bassBoost.roundedStrength
        set(value) = bassBoost.setStrength(value)

    inline var reverbPreset
        get() = reverb.preset
        set(value) {
            reverb.preset = value
        }

    fun seekTo(position: Long) {
        resetAudioSessionId()
        player.seekTo(position)
    }

    fun seekTo10SecsBack() = seekTo(
        maxOf(currentPosition - TEN_SECS_AS_MILLIS, 0)
    )

    fun seekTo10SecsForward(videoLength: Long) = seekTo(
        minOf(
            currentPosition + TEN_SECS_AS_MILLIS,
            videoLength
        )
    )

    fun seekToTrackAtDefaultPosition(index: Int) = player.seekToDefaultPosition(index)

    inline val hasPreviousMediaItem
        get() = player.hasPreviousMediaItem()

    inline val hasNextMediaItem
        get() = player.hasNextMediaItem()

    inline val previousMediaItemIndex
        get() = player.previousMediaItemIndex

    inline val nextMediaItemIndex
        get() = player.nextMediaItemIndex

    fun resumePlayback() {
        resetAudioSessionId()
        play()
    }

    fun seekToNextMediaItem() = player.seekToNextMediaItem()

    fun addMediaItem(mediaItem: MediaItem) = player.addMediaItem(mediaItem)

    fun removeMediaItem(index: Int) = player.removeMediaItem(index)

    fun setMediaItems(
        mediaItems: List<MediaItem>,
        startIndex: Int,
        startPosition: Long
    ) = player.setMediaItems(mediaItems, startIndex, startPosition)

    fun releaseAudioEffects() {
        equalizer.release()
        bassBoost.release()
        reverb.release()
        equalizerDataState.update { null }
    }

    fun getRepeatMode(isRepeating: Boolean) = when {
        isRepeating -> Player.REPEAT_MODE_ONE
        else -> when (playbackType) {
            PlaybackType.STREAM -> Player.REPEAT_MODE_OFF
            PlaybackType.TRACKS -> Player.REPEAT_MODE_ALL
        }
    }
}