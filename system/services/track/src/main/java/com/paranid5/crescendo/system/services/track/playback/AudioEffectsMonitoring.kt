package com.paranid5.crescendo.system.services.track.playback

import androidx.media3.common.PlaybackParameters
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.system.services.common.playback.AudioEffectsController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal suspend fun TrackService.startPlaybackEffectsMonitoring() =
    combine(
        playerProvider.areAudioEffectsEnabledFlow,
        playerProvider.pitchFlow,
        playerProvider.speedFlow,
    ) { enabled, pitch, speed ->
        Triple(enabled, pitch, speed)
    }.distinctUntilChanged().collectLatest { (enabled, pitch, speed) ->
        resetPlaybackEffects(
            audioEffectsController = playerProvider,
            playerProvider = playerProvider,
            isEnabled = enabled,
            pitch = pitch,
            speed = speed
        )
    }

internal suspend fun TrackService.startEqMonitoring() =
    combine(
        playerProvider.equalizerBandsFlow,
        playerProvider.equalizerPresetFlow,
        playerProvider.equalizerParamFlow,
    ) { param, bands, preset ->
        Triple(param, bands, preset)
    }.distinctUntilChanged().collectLatest { (bands, preset, param) ->
        playerProvider.setEqParameter(bands, preset, param)
    }

internal suspend fun TrackService.startBassMonitoring() =
    playerProvider
        .bassStrengthFlow
        .distinctUntilChanged()
        .collectLatest { playerProvider.setBassStrength(bassStrength = it) }

internal suspend fun TrackService.startReverbMonitoring() =
    playerProvider
        .reverbPresetFlow
        .distinctUntilChanged()
        .collectLatest { playerProvider.setReverbPreset(reverbPreset = it) }

private fun resetPlaybackEffects(
    audioEffectsController: AudioEffectsController,
    playerProvider: PlayerProvider,
    isEnabled: Boolean,
    speed: Float,
    pitch: Float,
) {
    playerProvider.playbackParameters = when {
        isEnabled -> PlaybackParameters(speed, pitch)
        else -> PlaybackParameters(1F, 1F)
    }

    runCatching {
        audioEffectsController.equalizer.enabled = isEnabled
        audioEffectsController.bassBoost.enabled = isEnabled
        // звук искажается
        // audioEffectsController.reverb.enabled = isEnabled
    }.onFailure { it.printStackTrace() }
}