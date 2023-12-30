package com.paranid5.crescendo.presentation.main.tracks.states

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentTrackFlow
import com.paranid5.crescendo.data.properties.storeTrackOrder
import com.paranid5.crescendo.data.properties.trackOrderFlow
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.domain.tracks.TrackOrder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TracksStateHolder(private val storageHandler: StorageHandler) {
    private val _tracksState: MutableStateFlow<ImmutableList<Track>> by lazy {
        MutableStateFlow(persistentListOf())
    }

    val tracksState by lazy {
        _tracksState.asStateFlow()
    }

    fun setTracks(tracks: ImmutableList<Track>) =
        _tracksState.update { tracks }

    private val _filteredTracksState: MutableStateFlow<ImmutableList<Track>> by lazy {
        MutableStateFlow(persistentListOf())
    }

    val filteredTracksState by lazy {
        _filteredTracksState.asStateFlow()
    }

    fun setFilteredTracks(tracks: ImmutableList<Track>) =
        _filteredTracksState.update { tracks }

    val trackOrderFlow by lazy {
        storageHandler.trackOrderFlow
    }

    suspend fun setTrackOrder(trackOrder: TrackOrder) =
        storageHandler.storeTrackOrder(trackOrder)

    val currentTrackFlow by lazy {
        storageHandler.currentTrackFlow
    }
}