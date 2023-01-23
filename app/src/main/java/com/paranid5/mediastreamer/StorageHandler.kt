package com.paranid5.mediastreamer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StorageHandler(private val context: Context) : CoroutineScope by MainScope() {
    companion object {
        private val CURRENT_URL = stringPreferencesKey("current_url")
        private val PLAYBACK_POSITION = longPreferencesKey("playback_position")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "params")

    val currentUrl = context.dataStore.data
        .map { preferences -> preferences[CURRENT_URL] }
        .map { it ?: "" }
        .stateIn(this, SharingStarted.Eagerly, "")

    internal suspend inline fun storeCurrentUrl(url: String) {
        context.dataStore.edit { preferences -> preferences[CURRENT_URL] = url }
    }

    val playbackPosition = context.dataStore.data
        .map { preferences -> preferences[PLAYBACK_POSITION] }
        .map { it ?: 0 }
        .stateIn(this, SharingStarted.WhileSubscribed(), 0)

    internal suspend inline fun storePlaybackPosition(position: Long) {
        context.dataStore.edit { preferences -> preferences[PLAYBACK_POSITION] = position }
    }
}