package com.paranid5.crescendo.data.states

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WaveformStateProvider(private val dataStore: DataStore<Preferences>) {
    private companion object {
        private val AMPLITUDES = stringPreferencesKey("amplitudes")
    }

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    @OptIn(ExperimentalCoroutinesApi::class)
    val amplitudesFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[AMPLITUDES] }
            .mapLatest { amplitudesStr -> amplitudesStr?.let(json::decodeAmplitudes) }
            .mapLatest { it ?: emptyList() }
    }

    suspend fun storeAmplitudes(amplitudes: List<Int>) {
        dataStore.edit { preferences ->
            preferences[AMPLITUDES] = json.encodeToString(amplitudes)
        }
    }
}

private fun Json.decodeAmplitudes(amplitudesStr: String) =
    decodeFromString<List<Int>>(amplitudesStr)