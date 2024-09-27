package com.paranid5.crescendo.system.services.stream.playback

import androidx.annotation.MainThread
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsEnabledDataSource
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsRepository
import com.paranid5.crescendo.domain.audio_effects.BassStrengthDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerBandsDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerParamDataSource
import com.paranid5.crescendo.domain.audio_effects.EqualizerPresetDataSource
import com.paranid5.crescendo.domain.audio_effects.PitchDataSource
import com.paranid5.crescendo.domain.audio_effects.ReverbPresetDataSource
import com.paranid5.crescendo.domain.audio_effects.SpeedDataSource
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.StreamPlaybackPositionPublisher
import com.paranid5.crescendo.domain.playback.StreamPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.stream.CurrentMetadataPublisher
import com.paranid5.crescendo.domain.stream.CurrentMetadataSubscriber
import com.paranid5.crescendo.domain.stream.PlayingStreamUrlPublisher
import com.paranid5.crescendo.domain.stream.PlayingStreamUrlSubscriber
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.system.services.stream.StreamService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class PlayerProvider(
    service: StreamService,
    audioEffectsRepository: AudioEffectsRepository,
    playbackRepository: PlaybackRepository,
    streamRepository: StreamRepository,
) : PlayerController by PlayerControllerImpl(
    service = service,
    audioEffectsRepository = audioEffectsRepository,
    playbackRepository = playbackRepository,
),
    AudioEffectsEnabledDataSource by audioEffectsRepository,
    SpeedDataSource by audioEffectsRepository,
    PitchDataSource by audioEffectsRepository,
    EqualizerBandsDataSource by audioEffectsRepository,
    EqualizerPresetDataSource by audioEffectsRepository,
    EqualizerParamDataSource by audioEffectsRepository,
    BassStrengthDataSource by audioEffectsRepository,
    ReverbPresetDataSource by audioEffectsRepository,
    PlayingStreamUrlSubscriber by streamRepository,
    PlayingStreamUrlPublisher by streamRepository,
    CurrentMetadataSubscriber by streamRepository,
    CurrentMetadataPublisher by streamRepository,
    StreamPlaybackPositionSubscriber by playbackRepository,
    StreamPlaybackPositionPublisher by playbackRepository {
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
        updateStreamPlaybackPosition(currentPosition)

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