package com.paranid5.crescendo.data.datastore

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class StreamDataStore(dataStoreProvider: DataStoreProvider) {
    private companion object {
        private val STREAM_URL = stringPreferencesKey("current_url")
        private val DOWNLOADING_URL = stringPreferencesKey("download_url")
        private val CURRENT_METADATA = stringPreferencesKey("current_metadata")

        private const val UNDEFINED_STREAM_URL = ""
    }

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    private val dataStore by lazy { dataStoreProvider.dataStore }

    val playingUrlFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[STREAM_URL] }
            .map { it ?: UNDEFINED_STREAM_URL }
    }

    suspend fun storePlayingUrl(url: String) {
        dataStore.edit { preferences -> preferences[STREAM_URL] = url }
    }

    val downloadingUrlFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[DOWNLOADING_URL] }
            .map { it ?: UNDEFINED_STREAM_URL }
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
