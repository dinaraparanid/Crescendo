package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import kotlinx.coroutines.flow.map

val DataStoreProvider.areAudioEffectsEnabledFlow
    get() = audioEffectsStateDataSource.areAudioEffectsEnabledFlow

suspend fun DataStoreProvider.storeAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) =
    audioEffectsStateDataSource.storeAudioEffectsEnabled(areAudioEffectsEnabled)

val DataStoreProvider.pitchFlow
    get() = audioEffectsStateDataSource.pitchFlow

val DataStoreProvider.pitchTextFlow
    get() = pitchFlow.map(Float::toString)

suspend fun DataStoreProvider.storePitch(pitch: Float) =
    audioEffectsStateDataSource.storePitch(pitch)

val DataStoreProvider.speedFlow
    get() = audioEffectsStateDataSource.speedFlow

val DataStoreProvider.speedTextFlow
    get() = speedFlow.map(Float::toString)

suspend fun DataStoreProvider.storeSpeed(speed: Float) =
    audioEffectsStateDataSource.storeSpeed(speed)

val DataStoreProvider.equalizerBandsFlow
    get() = audioEffectsStateDataSource.equalizerBandsFlow

suspend fun DataStoreProvider.storeEqualizerBands(bands: List<Short>) =
    audioEffectsStateDataSource.storeEqualizerBands(bands)

val DataStoreProvider.equalizerPresetFlow
    get() = audioEffectsStateDataSource.equalizerPresetFlow

suspend fun DataStoreProvider.storeEqualizerPreset(preset: Short) =
    audioEffectsStateDataSource.storeEqualizerPreset(preset)

val DataStoreProvider.equalizerParamFlow
    get() = audioEffectsStateDataSource.equalizerParamFlow

suspend fun DataStoreProvider.storeEqualizerParam(param: EqualizerBandsPreset) =
    audioEffectsStateDataSource.storeEqualizerParam(param)

val DataStoreProvider.bassStrengthFlow
    get() = audioEffectsStateDataSource.bassStrengthFlow

suspend fun DataStoreProvider.storeBassStrength(bassStrength: Short) =
    audioEffectsStateDataSource.storeBassStrength(bassStrength)

val DataStoreProvider.reverbPresetFlow
    get() = audioEffectsStateDataSource.reverbPresetFlow

suspend fun DataStoreProvider.storeReverbPreset(reverbPreset: Short) =
    audioEffectsStateDataSource.storeReverbPreset(reverbPreset)