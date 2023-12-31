package com.paranid5.crescendo.services.stream_service.playback.effects

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.paranid5.crescendo.services.stream_service.StreamService2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

suspend fun StreamService2.startPlaybackEffectsMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        combine(
            effectsController.areAudioEffectsEnabledFlow,
            effectsController.pitchFlow,
            effectsController.speedFlow,
        ) { enabled, pitch, speed ->
            Triple(enabled, pitch, speed)
        }.collectLatest { (enabled, pitch, speed) ->
            resetPlaybackEffects(
                audioEffectsController = effectsController,
                player = playerProvider.player,
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
        }.collectLatest { (bands, preset, param) ->
            effectsController.setEqParameter(bands, preset, param)
        }
    }

suspend fun StreamService2.startBassMonitoring() =
    effectsController.bassStrengthFlow.collectLatest {
        effectsController.setBassStrength(it)
    }

suspend fun StreamService2.startReverbMonitoring() =
    effectsController.reverbPresetFlow.collectLatest {
        effectsController.setReverbPreset(it)
    }

private inline val StreamService2.effectsController
    get() = playerProvider.playerController.audioEffectsController

private fun resetPlaybackEffects(
    audioEffectsController: AudioEffectsController,
    player: Player,
    isEnabled: Boolean,
    speed: Float,
    pitch: Float,
) {
    player.playbackParameters = when {
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