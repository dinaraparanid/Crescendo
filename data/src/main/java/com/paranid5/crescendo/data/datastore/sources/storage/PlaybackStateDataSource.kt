package com.paranid5.crescendo.data.datastore.sources.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.paranid5.crescendo.core.common.AudioStatus
import kotlinx.coroutines.flow.map

class PlaybackStateDataSource(private val dataStore: DataStore<Preferences>) {
    private companion object {
        private val TRACKS_PLAYBACK_POSITION = longPreferencesKey("tracks_playback_position")
        private val STREAM_PLAYBACK_POSITION = longPreferencesKey("stream_playback_position")

        private val IS_REPEATING = booleanPreferencesKey("is_repeating")
        private val AUDIO_STATUS = intPreferencesKey("audio_status")
    }

    val tracksPlaybackPositionFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[TRACKS_PLAYBACK_POSITION] }
            .map { it ?: 0 }
    }

    suspend fun storeTracksPlaybackPosition(position: Long) {
        dataStore.edit { preferences ->
            preferences[TRACKS_PLAYBACK_POSITION] = position
        }
    }

    val streamPlaybackPositionFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[STREAM_PLAYBACK_POSITION] }
            .map { it ?: 0 }
    }

    suspend fun storeStreamPlaybackPosition(position: Long) {
        dataStore.edit { preferences ->
            preferences[STREAM_PLAYBACK_POSITION] = position
        }
    }

    val isRepeatingFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[IS_REPEATING] }
            .map { it ?: false }
    }

    suspend fun storeIsRepeating(isRepeating: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_REPEATING] = isRepeating
        }
    }

    val audioStatusFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[AUDIO_STATUS] }
            .map { audioStatusInd -> audioStatusInd?.let { AudioStatus.entries[it] } }
    }

    suspend fun storeAudioStatus(audioStatus: AudioStatus) {
        dataStore.edit { preferences ->
            preferences[AUDIO_STATUS] = audioStatus.ordinal
        }
    }
}