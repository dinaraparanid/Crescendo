package com.paranid5.crescendo.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.paranid5.crescendo.core.common.PlaybackStatus
import kotlinx.coroutines.flow.map

internal class PlaybackDataStore(dataStoreProvider: DataStoreProvider) {
    private companion object {
        private val TRACKS_PLAYBACK_POSITION = longPreferencesKey("tracks_playback_position")
        private val STREAM_PLAYBACK_POSITION = longPreferencesKey("stream_playback_position")

        private val IS_REPEATING = booleanPreferencesKey("is_repeating")
        private val AUDIO_STATUS = intPreferencesKey("audio_status")

        private const val INITIAL_PLAYBACK_POSITION = 0L
        private const val INITIAL_REPEATING = false
    }

    private val dataStore by lazy { dataStoreProvider.dataStore }

    val tracksPlaybackPositionFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[TRACKS_PLAYBACK_POSITION] }
            .map { it ?: INITIAL_PLAYBACK_POSITION }
    }

    suspend fun storeTracksPlaybackPosition(position: Long) {
        dataStore.edit { preferences ->
            preferences[TRACKS_PLAYBACK_POSITION] = position
        }
    }

    val streamPlaybackPositionFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[STREAM_PLAYBACK_POSITION] }
            .map { it ?: INITIAL_PLAYBACK_POSITION }
    }

    suspend fun storeStreamPlaybackPosition(position: Long) {
        dataStore.edit { preferences ->
            preferences[STREAM_PLAYBACK_POSITION] = position
        }
    }

    val isRepeatingFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[IS_REPEATING] }
            .map { it ?: INITIAL_REPEATING }
    }

    suspend fun storeRepeating(isRepeating: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_REPEATING] = isRepeating
        }
    }

    val playbackStatusFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[AUDIO_STATUS] }
            .map { audioStatusInd -> audioStatusInd?.let { PlaybackStatus.entries[it] } }
    }

    suspend fun storeAudioStatus(playbackStatus: PlaybackStatus) {
        dataStore.edit { preferences ->
            preferences[AUDIO_STATUS] = playbackStatus.ordinal
        }
    }
}