package com.paranid5.crescendo.services.stream_service.playback.effects

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.PlaybackParameters
import com.paranid5.crescendo.services.stream_service.StreamService2
import com.paranid5.crescendo.services.stream_service.playback.PlayerProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

suspend fun StreamService2.startPlaybackEffectsMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        combine(
            effectsController.areAudioEffectsEnabledFlow,
            effectsController.pitchFlow,
            effectsController.speedFlow,
        ) { enabled, pitch, speed ->
            Triple(enabled, pitch, speed)
        }.distinctUntilChanged().collectLatest { (enabled, pitch, speed) ->
            resetPlaybackEffects(
                audioEffectsController = effectsController,
                playerProvider = playerProvider,
                isEnabled = enabled,
                pitch = pitch,
                speed = speed
            )

            notificationManager.updateNotification()
        }
    }

suspend fun StreamService2.startEqMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        combine(
            effectsController.equalizerBandsFlow,
            effectsController.equalizerPresetFlow,
            effectsController.equalizerParamFlow,
        ) { param, bands, preset ->
            Triple(param, bands, preset)
        }.distinctUntilChanged().collectLatest { (bands, preset, param) ->
            effectsController.setEqParameter(bands, preset, param)
        }
    }

suspend fun StreamService2.startBassMonitoring() =
    effectsController
        .bassStrengthFlow
        .distinctUntilChanged()
        .collectLatest { effectsController.setBassStrength(it) }

suspend fun StreamService2.startReverbMonitoring() =
    effectsController
        .reverbPresetFlow
        .distinctUntilChanged()
        .collectLatest { effectsController.setReverbPreset(it) }

private inline val StreamService2.effectsController
    get() = playerProvider.playerController.audioEffectsController

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