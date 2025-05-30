package com.paranid5.crescendo.system.services.stream.playback

import androidx.media3.common.PlaybackParameters
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.system.services.common.playback.AudioEffectsController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal suspend fun StreamService.startPlaybackEffectsMonitoring() =
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

        notificationManager.invalidateNotification()
    }

internal suspend fun StreamService.startEqMonitoring() =
    combine(
        playerProvider.equalizerBandsFlow,
        playerProvider.equalizerPresetFlow,
        playerProvider.equalizerParamFlow,
    ) { param, bands, preset ->
        Triple(param, bands, preset)
    }.distinctUntilChanged().collectLatest { (bands, preset, param) ->
        playerProvider.setEqParameter(bands, preset, param)
    }

internal suspend fun StreamService.startBassMonitoring() =
    playerProvider
        .bassStrengthFlow
        .distinctUntilChanged()
        .collectLatest { playerProvider.setBassStrength(it) }

internal suspend fun StreamService.startReverbMonitoring() =
    playerProvider
        .reverbPresetFlow
        .distinctUntilChanged()
        .collectLatest { playerProvider.setReverbPreset(it) }

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
        audioEffectsController.reverb.enabled = isEnabled
    }
}