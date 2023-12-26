package com.paranid5.crescendo.data.states

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.paranid5.crescendo.domain.media.AudioStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

class PlaybackStateProvider(private val dataStore: DataStore<Preferences>) {
    private companion object {
        private val TRACKS_PLAYBACK_POSITION = longPreferencesKey("tracks_playback_position")
        private val STREAM_PLAYBACK_POSITION = longPreferencesKey("stream_playback_position")

        private val IS_REPEATING = booleanPreferencesKey("is_repeating")
        private val AUDIO_STATUS = intPreferencesKey("audio_status")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tracksPlaybackPositionFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[TRACKS_PLAYBACK_POSITION] }
            .mapLatest { it ?: 0 }
    }

    suspend fun storeTracksPlaybackPosition(position: Long) {
        dataStore.edit { preferences ->
            preferences[TRACKS_PLAYBACK_POSITION] = position
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val streamPlaybackPositionFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[STREAM_PLAYBACK_POSITION] }
            .mapLatest { it ?: 0 }
    }

    suspend fun storeStreamPlaybackPosition(position: Long) {
        dataStore.edit { preferences ->
            preferences[STREAM_PLAYBACK_POSITION] = position
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isRepeatingFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[IS_REPEATING] }
            .mapLatest { it ?: false }
    }

    suspend fun storeIsRepeating(isRepeating: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_REPEATING] = isRepeating
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val audioStatusFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[AUDIO_STATUS] }
            .mapLatest { audioStatusInd -> audioStatusInd?.let { AudioStatus.entries[it] } }
    }

    suspend fun storeAudioStatus(audioStatus: AudioStatus) {
        dataStore.edit { preferences ->
            preferences[AUDIO_STATUS] = audioStatus.ordinal
        }
    }
}