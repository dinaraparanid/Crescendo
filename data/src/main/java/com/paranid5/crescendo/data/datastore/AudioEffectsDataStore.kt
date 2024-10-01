package com.paranid5.crescendo.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class AudioEffectsDataStore(
    dataStoreProvider: DataStoreProvider,
    private val json: Json,
) {
    private companion object {
        private val AUDIO_EFFECTS_ENABLED = booleanPreferencesKey("audio_effects_enabled")
        private val PITCH_VALUE = floatPreferencesKey("pitch_value")
        private val SPEED_VALUE = floatPreferencesKey("speed_value")

        private val EQ_PARAM = intPreferencesKey("eq_param")
        private val EQ_BANDS = stringPreferencesKey("eq_bands")
        private val EQ_PRESET = intPreferencesKey("eq_preset")

        private val BASS_STRENGTH = intPreferencesKey("bass_strength")
        private val REVERB_PRESET = intPreferencesKey("reverb_preset")

        private const val INITIAL_PITCH = 1.0F
        private const val INITIAL_SPEED = 1.0F
        private const val INITIAL_EQ_PRESET = 0
        private const val INITIAL_BASS_STRENGTH: Short = 0
        private const val INITIAL_REVERB_PRESET: Short = 0
        private const val INITIAL_AUDIO_EFFECTS_ENABLED = false
    }

    private val dataStore by lazy { dataStoreProvider.dataStore }

    val areAudioEffectsEnabledFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[AUDIO_EFFECTS_ENABLED] }
            .map { it ?: INITIAL_AUDIO_EFFECTS_ENABLED }
    }

    suspend fun storeAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUDIO_EFFECTS_ENABLED] = areAudioEffectsEnabled
        }
    }

    val pitchFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[PITCH_VALUE] }
            .map { it ?: INITIAL_PITCH }
    }

    suspend fun storePitch(pitch: Float) {
        dataStore.edit { preferences ->
            preferences[PITCH_VALUE] = pitch
        }
    }

    val speedFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[SPEED_VALUE] }
            .map { it ?: INITIAL_SPEED }
    }

    suspend fun storeSpeed(speed: Float) {
        dataStore.edit { preferences ->
            preferences[SPEED_VALUE] = speed
        }
    }

    val equalizerBandsFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[EQ_BANDS] }
            .map { bandsStr -> bandsStr?.let(json::decodeEqBands) }
            .map { it ?: emptyList() }
    }

    suspend fun storeEqualizerBands(bands: List<Short>) {
        dataStore.edit { preferences ->
            preferences[EQ_BANDS] = json.encodeToString(bands)
        }
    }

    val equalizerPresetFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[EQ_PRESET] }
            .map { preset -> preset?.toShort() }
            .map { it ?: EqualizerData.NO_EQ_PRESET }
    }

    suspend fun storeEqualizerPreset(preset: Short) {
        dataStore.edit { preferences ->
            preferences[EQ_PRESET] = preset.toInt()
        }
    }

    val equalizerParamFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[EQ_PARAM] ?: INITIAL_EQ_PRESET }
            .map { param -> EqualizerBandsPreset.entries[param] }
    }

    suspend fun storeEqualizerParam(param: EqualizerBandsPreset) {
        dataStore.edit { preferences ->
            preferences[EQ_PARAM] = param.ordinal
        }
    }

    val bassStrengthFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[BASS_STRENGTH] }
            .map { strength -> strength?.toShort() ?: INITIAL_BASS_STRENGTH }
    }

    suspend fun storeBassStrength(bassStrength: Short) {
        dataStore.edit { preferences ->
            preferences[BASS_STRENGTH] = bassStrength.toInt()
        }
    }

    val reverbPresetFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[REVERB_PRESET] }
            .map { preset -> preset?.toShort() ?: INITIAL_REVERB_PRESET }
    }

    suspend fun storeReverbPreset(reverbPreset: Short) {
        dataStore.edit { preferences ->
            preferences[REVERB_PRESET] = reverbPreset.toInt()
        }
    }
}

private fun Json.decodeEqBands(bandsStr: String) =
    decodeFromString<List<Short>?>(bandsStr)
