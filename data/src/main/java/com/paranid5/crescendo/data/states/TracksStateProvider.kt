package com.paranid5.crescendo.data.states

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.domain.tracks.TrackOrder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TracksStateProvider(private val dataStore: DataStore<Preferences>) {
    private companion object {
        private val CURRENT_TRACK_INDEX = intPreferencesKey("current_track_index")
        private val CURRENT_PLAYLIST = stringPreferencesKey("current_playlist")
        private val TRACK_ORDER = byteArrayPreferencesKey("track_order")
    }

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentTrackIndexFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[CURRENT_TRACK_INDEX] }
            .mapLatest { it ?: 0 }
    }

    suspend fun storeCurrentTrackIndex(index: Int) {
        dataStore.edit { preferences ->
            preferences[CURRENT_TRACK_INDEX] = index
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentPlaylistFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[CURRENT_PLAYLIST] }
            .mapLatest { playlistStr -> playlistStr?.let(json::decodePlaylist) }
            .mapLatest { it ?: persistentListOf() }
    }

    suspend fun storeCurrentPlaylist(playlist: List<Track>) {
        dataStore.edit { preferences ->
            preferences[CURRENT_PLAYLIST] = json.encodeToString(playlist.map(::DefaultTrack))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val trackOrderFlow by lazy {
        dataStore.data
            .mapLatest { preferences -> preferences[TRACK_ORDER] }
            .mapLatest { bytes -> bytes?.let(::decodeTrackOrder) }
            .mapLatest { it ?: TrackOrder.default }
    }

    suspend fun storeTrackOrder(trackOrder: TrackOrder) {
        dataStore.edit { preferences ->
            preferences[TRACK_ORDER] = encodeTrackOrder(trackOrder)
        }
    }
}

private fun Json.decodePlaylist(playlistStr: String): ImmutableList<Track> =
    decodeFromString<List<DefaultTrack>>(playlistStr).toImmutableList()

private fun decodeTrackOrder(bytes: ByteArray): TrackOrder {
    val (contentOrder, orderType) = bytes

    return TrackOrder(
        contentOrder = TrackOrder.TrackContentOrder.entries[contentOrder.toInt()],
        orderType = TrackOrder.TrackOrderType.entries[orderType.toInt()]
    )
}

private fun encodeTrackOrder(trackOrder: TrackOrder) =
    byteArrayOf(
        trackOrder.contentOrder.ordinal.toByte(),
        trackOrder.orderType.ordinal.toByte()
    )