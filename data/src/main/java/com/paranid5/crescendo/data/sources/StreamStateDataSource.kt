package com.paranid5.crescendo.data.sources

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StreamStateDataSource(private val dataStore: DataStore<Preferences>) {
    private companion object {
        private val CURRENT_URL = stringPreferencesKey("current_url")
        private val CURRENT_METADATA = stringPreferencesKey("current_metadata")
    }

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUrlFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[CURRENT_URL] }
            .mapLatest { it ?: "" }
    }

    suspend fun storeCurrentUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_URL] = url
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMetadataFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[CURRENT_METADATA] }
            .mapLatest { metaString -> metaString?.let(json::decodeVideoMetadata) }
    }

    suspend fun storeCurrentMetadata(metadata: com.paranid5.crescendo.core.common.metadata.VideoMetadata?) {
        dataStore.edit { preferences ->
            preferences[CURRENT_METADATA] = json.encodeToString(metadata)
        }
    }
}

private fun Json.decodeVideoMetadata(metadata: String) =
    decodeFromString<com.paranid5.crescendo.core.common.metadata.VideoMetadata>(metadata)