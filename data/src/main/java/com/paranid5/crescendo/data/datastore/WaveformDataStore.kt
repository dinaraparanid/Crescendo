package com.paranid5.crescendo.data.datastore

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class WaveformDataStore(
    dataStoreProvider: DataStoreProvider,
    private val json: Json,
) {
    private companion object {
        private val AMPLITUDES = stringPreferencesKey("amplitudes")
    }

    private val dataStore by lazy { dataStoreProvider.dataStore }

    val amplitudesFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[AMPLITUDES] }
            .map { amplitudesStr -> amplitudesStr?.let(json::decodeAmplitudes) }
            .map { it ?: emptyList() }
    }

    suspend fun storeAmplitudes(amplitudes: List<Int>) {
        dataStore.edit { preferences ->
            preferences[AMPLITUDES] = json.encodeToString(amplitudes)
        }
    }
}

private fun Json.decodeAmplitudes(amplitudesStr: String) =
    decodeFromString<List<Int>>(amplitudesStr)
