package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

inline val StorageRepository.areAudioEffectsEnabledFlow
    get() = audioEffectsStateDataSource.areAudioEffectsEnabledFlow

suspend inline fun StorageRepository.storeAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) =
    audioEffectsStateDataSource.storeAudioEffectsEnabled(areAudioEffectsEnabled)

inline val StorageRepository.pitchFlow
    get() = audioEffectsStateDataSource.pitchFlow

@OptIn(ExperimentalCoroutinesApi::class)
inline val StorageRepository.pitchTextFlow
    get() = pitchFlow.mapLatest { it.toString() }

suspend inline fun StorageRepository.storePitch(pitch: Float) =
    audioEffectsStateDataSource.storePitch(pitch)

inline val StorageRepository.speedFlow
    get() = audioEffectsStateDataSource.speedFlow

@OptIn(ExperimentalCoroutinesApi::class)
inline val StorageRepository.speedTextFlow
    get() = speedFlow.mapLatest { it.toString() }

suspend inline fun StorageRepository.storeSpeed(speed: Float) =
    audioEffectsStateDataSource.storeSpeed(speed)

inline val StorageRepository.equalizerBandsFlow
    get() = audioEffectsStateDataSource.equalizerBandsFlow

suspend inline fun StorageRepository.storeEqualizerBands(bands: ImmutableList<Short>) =
    audioEffectsStateDataSource.storeEqualizerBands(bands)

inline val StorageRepository.equalizerPresetFlow
    get() = audioEffectsStateDataSource.equalizerPresetFlow

suspend inline fun StorageRepository.storeEqualizerPreset(preset: Short) =
    audioEffectsStateDataSource.storeEqualizerPreset(preset)

inline val StorageRepository.equalizerParamFlow
    get() = audioEffectsStateDataSource.equalizerParamFlow

suspend inline fun StorageRepository.storeEqualizerParam(param: com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset) =
    audioEffectsStateDataSource.storeEqualizerParam(param)

inline val StorageRepository.bassStrengthFlow
    get() = audioEffectsStateDataSource.bassStrengthFlow

suspend inline fun StorageRepository.storeBassStrength(bassStrength: Short) =
    audioEffectsStateDataSource.storeBassStrength(bassStrength)

inline val StorageRepository.reverbPresetFlow
    get() = audioEffectsStateDataSource.reverbPresetFlow

suspend inline fun StorageRepository.storeReverbPreset(reverbPreset: Short) =
    audioEffectsStateDataSource.storeReverbPreset(reverbPreset)