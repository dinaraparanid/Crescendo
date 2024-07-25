package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import kotlinx.coroutines.flow.map

val StorageRepository.areAudioEffectsEnabledFlow
    get() = audioEffectsStateDataSource.areAudioEffectsEnabledFlow

suspend fun StorageRepository.storeAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) =
    audioEffectsStateDataSource.storeAudioEffectsEnabled(areAudioEffectsEnabled)

val StorageRepository.pitchFlow
    get() = audioEffectsStateDataSource.pitchFlow

val StorageRepository.pitchTextFlow
    get() = pitchFlow.map(Float::toString)

suspend fun StorageRepository.storePitch(pitch: Float) =
    audioEffectsStateDataSource.storePitch(pitch)

val StorageRepository.speedFlow
    get() = audioEffectsStateDataSource.speedFlow

val StorageRepository.speedTextFlow
    get() = speedFlow.map(Float::toString)

suspend fun StorageRepository.storeSpeed(speed: Float) =
    audioEffectsStateDataSource.storeSpeed(speed)

val StorageRepository.equalizerBandsFlow
    get() = audioEffectsStateDataSource.equalizerBandsFlow

suspend fun StorageRepository.storeEqualizerBands(bands: List<Short>) =
    audioEffectsStateDataSource.storeEqualizerBands(bands)

val StorageRepository.equalizerPresetFlow
    get() = audioEffectsStateDataSource.equalizerPresetFlow

suspend fun StorageRepository.storeEqualizerPreset(preset: Short) =
    audioEffectsStateDataSource.storeEqualizerPreset(preset)

val StorageRepository.equalizerParamFlow
    get() = audioEffectsStateDataSource.equalizerParamFlow

suspend fun StorageRepository.storeEqualizerParam(param: EqualizerBandsPreset) =
    audioEffectsStateDataSource.storeEqualizerParam(param)

val StorageRepository.bassStrengthFlow
    get() = audioEffectsStateDataSource.bassStrengthFlow

suspend fun StorageRepository.storeBassStrength(bassStrength: Short) =
    audioEffectsStateDataSource.storeBassStrength(bassStrength)

val StorageRepository.reverbPresetFlow
    get() = audioEffectsStateDataSource.reverbPresetFlow

suspend fun StorageRepository.storeReverbPreset(reverbPreset: Short) =
    audioEffectsStateDataSource.storeReverbPreset(reverbPreset)