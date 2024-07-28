package com.paranid5.crescendo.data.datastore.sources.storage

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
        private val PLAYING_URL = stringPreferencesKey("current_url")
        private val DOWNLOADING_URL = stringPreferencesKey("download_url")
        private val CURRENT_METADATA = stringPreferencesKey("current_metadata")
    }

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    val playingUrlFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[PLAYING_URL] }
            .map { it ?: "" }
    }

    suspend fun storePlayingUrl(url: String) {
        dataStore.edit { preferences -> preferences[PLAYING_URL] = url }
    }

    val downloadingUrlFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[DOWNLOADING_URL] }
            .map { it ?: "" }
    }

    suspend fun storeDownloadingUrl(url: String) {
        dataStore.edit { preferences -> preferences[DOWNLOADING_URL] = url }
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