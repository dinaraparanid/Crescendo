package com.paranid5.crescendo.data.datastore.sources.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.paranid5.crescendo.core.common.tracks.TrackOrder
import kotlinx.coroutines.flow.map

class TracksStateDataSource(private val dataStore: DataStore<Preferences>) {
    private companion object {
        private val CURRENT_TRACK_INDEX = intPreferencesKey("current_track_index")
        private val TRACK_ORDER = byteArrayPreferencesKey("track_order")
    }

    val currentTrackIndexFlow by lazy {
        dataStore.data.map { preferences -> preferences[CURRENT_TRACK_INDEX] ?: 0 }
    }

    suspend fun storeCurrentTrackIndex(index: Int) {
        dataStore.edit { preferences ->
            preferences[CURRENT_TRACK_INDEX] = index
        }
    }

    val trackOrderFlow by lazy {
        dataStore.data
            .map { preferences -> preferences[TRACK_ORDER] }
            .map { bytes -> bytes?.let(::decodeTrackOrder) }
            .map { it ?: TrackOrder.default }
    }

    suspend fun storeTrackOrder(trackOrder: TrackOrder) {
        dataStore.edit { preferences ->
            preferences[TRACK_ORDER] = encodeTrackOrder(trackOrder)
        }
    }
}

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