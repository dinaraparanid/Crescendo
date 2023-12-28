package com.paranid5.crescendo.presentation.main.audio_effects.properties

import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel

inline val AudioEffectsViewModel.areAudioEffectsEnabledFlow
    get() = effectsStateHolder.areAudioEffectsEnabledFlow

inline val AudioEffectsViewModel.bassStrengthFlow
    get() = effectsStateHolder.bassStrengthFlow

inline val AudioEffectsViewModel.reverbPresetFlow
    get() = effectsStateHolder.reverbPresetFlow

inline val AudioEffectsViewModel.pitchTextFlow
    get() = effectsStateHolder.pitchTextFlow

inline val AudioEffectsViewModel.speedTextState
    get() = effectsStateHolder.speedTextState

suspend inline fun AudioEffectsViewModel.storePitch(pitch: Float) =
    effectsStateHolder.storePitch(pitch)

suspend inline fun AudioEffectsViewModel.storeSpeed(speed: Float) =
    effectsStateHolder.storeSpeed(speed)