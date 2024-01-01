package com.paranid5.crescendo.services.stream_service.playback

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.PlaybackParameters
import com.paranid5.crescendo.services.stream_service.StreamService
import com.paranid5.crescendo.services.core.playback.AudioEffectsController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

suspend fun StreamService.startPlaybackEffectsMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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

            notificationManager.updateNotification()
        }
    }

suspend fun StreamService.startEqMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        combine(
            playerProvider.equalizerBandsFlow,
            playerProvider.equalizerPresetFlow,
            playerProvider.equalizerParamFlow,
        ) { param, bands, preset ->
            Triple(param, bands, preset)
        }.distinctUntilChanged().collectLatest { (bands, preset, param) ->
            playerProvider.setEqParameter(bands, preset, param)
        }
    }

suspend fun StreamService.startBassMonitoring() =
    playerProvider
        .bassStrengthFlow
        .distinctUntilChanged()
        .collectLatest { playerProvider.setBassStrength(it) }

suspend fun StreamService.startReverbMonitoring() =
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

    // For some reason, it requires multiple tries to enable...
    repeat(3) {
        try {
            audioEffectsController.equalizer.enabled = isEnabled
            audioEffectsController.bassBoost.enabled = isEnabled
            audioEffectsController.reverb.enabled = isEnabled
        } catch (ignored: IllegalStateException) {
            // not initialized
        }
    }
}