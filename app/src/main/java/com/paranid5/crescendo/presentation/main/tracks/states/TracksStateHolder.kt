package com.paranid5.crescendo.presentation.main.tracks.states

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.tracks.Track
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface TracksStateHolder {
    val tracksState: StateFlow<ImmutableList<Track>>
    val filteredTracksState: StateFlow<ImmutableList<Track>>

    fun setTracks(tracks: ImmutableList<Track>)
    fun setFilteredTracks(tracks: ImmutableList<Track>)
}

class TracksStateHolderImpl(private val storageHandler: StorageHandler) : TracksStateHolder {
    private val _tracksState: MutableStateFlow<ImmutableList<Track>> by lazy {
        MutableStateFlow(persistentListOf())
    }

    override val tracksState by lazy {
        _tracksState.asStateFlow()
    }

    override fun setTracks(tracks: ImmutableList<Track>) =
        _tracksState.update { tracks }

    private val _filteredTracksState: MutableStateFlow<ImmutableList<Track>> by lazy {
        MutableStateFlow(persistentListOf())
    }

    override val filteredTracksState by lazy {
        _filteredTracksState.asStateFlow()
    }

    override fun setFilteredTracks(tracks: ImmutableList<Track>) =
        _filteredTracksState.update { tracks }
}