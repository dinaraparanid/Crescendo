package com.paranid5.mediastreamer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class StorageHandler(private val context: Context) {
    companion object {
        private val CURRENT_URL = stringPreferencesKey("current_url")
        private val PLAYBACK_POSITION = longPreferencesKey("playback_position")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "params")

    internal inline val currentUrl
        get() = context.dataStore.data
            .map { preferences -> preferences[CURRENT_URL] }
            .map { it ?: "" }

    internal suspend inline fun storeCurrentUrl(url: String) {
        context.dataStore.edit { preferences -> preferences[CURRENT_URL] = url }
    }

    internal inline val playbackPosition
        get() = context.dataStore.data
            .map { preferences -> preferences[PLAYBACK_POSITION] }
            .map { it ?: 0 }

    internal suspend inline fun storePlaybackPosition(position: Long) {
        context.dataStore.edit { preferences -> preferences[PLAYBACK_POSITION] = position }
    }
}