package com.paranid5.crescendo.data.sources.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StreamStateDataSource(private val dataStore: DataStore<Preferences>) {
    private companion object {
        private val CURRENT_URL = stringPreferencesKey("current_url")
        private val CURRENT_METADATA = stringPreferencesKey("current_metadata")
    }

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    val currentUrlFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[CURRENT_URL] }
            .map { it ?: "" }
    }

    suspend fun storeCurrentUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_URL] = url
        }
    }

    val currentMetadataFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[CURRENT_METADATA] }
            .map { metaString -> metaString?.let(json::decodeVideoMetadata) }
    }

    suspend fun storeCurrentMetadata(metadata: VideoMetadata?) {
        dataStore.edit { preferences ->
            preferences[CURRENT_METADATA] = json.encodeToString(metadata)
        }
    }
}

private fun Json.decodeVideoMetadata(metadata: String) =
    decodeFromString<VideoMetadata>(metadata)