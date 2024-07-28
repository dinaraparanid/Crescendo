package com.paranid5.crescendo.data.datastore.sources.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WaveformStateDataSource(private val dataStore: DataStore<Preferences>) {
    private companion object {
        private val AMPLITUDES = stringPreferencesKey("amplitudes")
    }

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    val amplitudesFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[AMPLITUDES] }
            .map { amplitudesStr -> amplitudesStr?.let(json::decodeAmplitudes) }
            .map { it ?: persistentListOf() }
    }

    suspend fun storeAmplitudes(amplitudes: ImmutableList<Int>) {
        dataStore.edit { preferences ->
            preferences[AMPLITUDES] = json.encodeToString(amplitudes.toList())
        }
    }
}

private fun Json.decodeAmplitudes(amplitudesStr: String) =
    decodeFromString<List<Int>>(amplitudesStr).toImmutableList()