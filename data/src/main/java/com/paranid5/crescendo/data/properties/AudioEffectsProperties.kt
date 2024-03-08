package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

inline val StorageHandler.areAudioEffectsEnabledFlow
    get() = audioEffectsStateProvider.areAudioEffectsEnabledFlow

suspend inline fun StorageHandler.storeAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) =
    audioEffectsStateProvider.storeAudioEffectsEnabled(areAudioEffectsEnabled)

inline val StorageHandler.pitchFlow
    get() = audioEffectsStateProvider.pitchFlow

@OptIn(ExperimentalCoroutinesApi::class)
inline val StorageHandler.pitchTextFlow
    get() = pitchFlow.mapLatest { it.toString() }

suspend inline fun StorageHandler.storePitch(pitch: Float) =
    audioEffectsStateProvider.storePitch(pitch)

inline val StorageHandler.speedFlow
    get() = audioEffectsStateProvider.speedFlow

@OptIn(ExperimentalCoroutinesApi::class)
inline val StorageHandler.speedTextFlow
    get() = speedFlow.mapLatest { it.toString() }

suspend inline fun StorageHandler.storeSpeed(speed: Float) =
    audioEffectsStateProvider.storeSpeed(speed)

inline val StorageHandler.equalizerBandsFlow
    get() = audioEffectsStateProvider.equalizerBandsFlow

suspend inline fun StorageHandler.storeEqualizerBands(bands: ImmutableList<Short>) =
    audioEffectsStateProvider.storeEqualizerBands(bands)

inline val StorageHandler.equalizerPresetFlow
    get() = audioEffectsStateProvider.equalizerPresetFlow

suspend inline fun StorageHandler.storeEqualizerPreset(preset: Short) =
    audioEffectsStateProvider.storeEqualizerPreset(preset)

inline val StorageHandler.equalizerParamFlow
    get() = audioEffectsStateProvider.equalizerParamFlow

suspend inline fun StorageHandler.storeEqualizerParam(param: com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset) =
    audioEffectsStateProvider.storeEqualizerParam(param)

inline val StorageHandler.bassStrengthFlow
    get() = audioEffectsStateProvider.bassStrengthFlow

suspend inline fun StorageHandler.storeBassStrength(bassStrength: Short) =
    audioEffectsStateProvider.storeBassStrength(bassStrength)

inline val StorageHandler.reverbPresetFlow
    get() = audioEffectsStateProvider.reverbPresetFlow

suspend inline fun StorageHandler.storeReverbPreset(reverbPreset: Short) =
    audioEffectsStateProvider.storeReverbPreset(reverbPreset)