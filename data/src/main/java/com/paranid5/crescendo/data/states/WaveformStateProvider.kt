package com.paranid5.crescendo.data.states

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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
            .mapLatest { it ?: persistentListOf() }
    }

    suspend fun storeAmplitudes(amplitudes: ImmutableList<Int>) {
        dataStore.edit { preferences ->
            preferences[AMPLITUDES] = json.encodeToString(amplitudes.toList())
        }
    }
}

private fun Json.decodeAmplitudes(amplitudesStr: String) =
    decodeFromString<List<Int>>(amplitudesStr).toImmutableList()