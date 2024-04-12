package com.paranid5.crescendo.data.sources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AudioEffectsStateDataSource(private val dataStore: DataStore<Preferences>) {
    private companion object {
        private val AUDIO_EFFECTS_ENABLED = booleanPreferencesKey("audio_effects_enabled")
        private val PITCH_VALUE = floatPreferencesKey("pitch_value")
        private val SPEED_VALUE = floatPreferencesKey("speed_value")

        private val EQ_PARAM = intPreferencesKey("eq_param")
        private val EQ_BANDS = stringPreferencesKey("eq_bands")
        private val EQ_PRESET = intPreferencesKey("eq_preset")

        private val BASS_STRENGTH = intPreferencesKey("bass_strength")
        private val REVERB_PRESET = intPreferencesKey("reverb_preset")
    }

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    @OptIn(ExperimentalCoroutinesApi::class)
    val areAudioEffectsEnabledFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[AUDIO_EFFECTS_ENABLED] }
            .mapLatest { it ?: false }
    }

    suspend fun storeAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUDIO_EFFECTS_ENABLED] = areAudioEffectsEnabled
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pitchFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[PITCH_VALUE] }
            .mapLatest { it ?: 1.0F }
    }

    suspend fun storePitch(pitch: Float) {
        dataStore.edit { preferences ->
            preferences[PITCH_VALUE] = pitch
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val speedFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[SPEED_VALUE] }
            .mapLatest { it ?: 1.0F }
    }

    suspend fun storeSpeed(speed: Float) {
        dataStore.edit { preferences ->
            preferences[SPEED_VALUE] = speed
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val equalizerBandsFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[EQ_BANDS] }
            .mapLatest { bandsStr -> bandsStr?.let(json::decodeEqBands) }
            .mapLatest { it ?: persistentListOf() }
    }

    suspend fun storeEqualizerBands(bands: ImmutableList<Short>) {
        dataStore.edit { preferences ->
            preferences[EQ_BANDS] = json.encodeToString(bands.toList())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val equalizerPresetFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[EQ_PRESET] }
            .mapLatest { preset -> preset?.toShort() }
            .mapLatest { it ?: com.paranid5.crescendo.core.common.eq.EqualizerData.NO_EQ_PRESET }
    }

    suspend fun storeEqualizerPreset(preset: Short) {
        dataStore.edit { preferences ->
            preferences[EQ_PRESET] = preset.toInt()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val equalizerParamFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[EQ_PARAM] ?: 0 }
            .mapLatest { param -> com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset.entries[param] }
    }

    suspend fun storeEqualizerParam(param: com.paranid5.crescendo.core.common.eq.EqualizerBandsPreset) {
        dataStore.edit { preferences ->
            preferences[EQ_PARAM] = param.ordinal
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val bassStrengthFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[BASS_STRENGTH] }
            .mapLatest { strength -> strength?.toShort() ?: 0 }
    }

    suspend fun storeBassStrength(bassStrength: Short) {
        dataStore.edit { preferences ->
            preferences[BASS_STRENGTH] = bassStrength.toInt()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val reverbPresetFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[REVERB_PRESET] }
            .mapLatest { preset -> preset?.toShort() ?: 0 }
    }

    suspend fun storeReverbPreset(reverbPreset: Short) {
        dataStore.edit { preferences ->
            preferences[REVERB_PRESET] = reverbPreset.toInt()
        }
    }
}

private fun Json.decodeEqBands(bandsStr: String) =
    decodeFromString<List<Short>?>(bandsStr)?.toImmutableList()