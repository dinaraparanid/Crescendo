package com.paranid5.crescendo.presentation.main.tracks.states

import com.paranid5.crescendo.data.StorageRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface TracksStateHolder {
    val tracksState: StateFlow<ImmutableList<com.paranid5.crescendo.core.common.tracks.Track>>
    val filteredTracksState: StateFlow<ImmutableList<com.paranid5.crescendo.core.common.tracks.Track>>

    fun setTracks(tracks: ImmutableList<com.paranid5.crescendo.core.common.tracks.Track>)
    fun setFilteredTracks(tracks: ImmutableList<com.paranid5.crescendo.core.common.tracks.Track>)
}

class TracksStateHolderImpl(private val storageRepository: StorageRepository) : TracksStateHolder {
    private val _tracksState: MutableStateFlow<ImmutableList<com.paranid5.crescendo.core.common.tracks.Track>> by lazy {
        MutableStateFlow(persistentListOf())
    }

    override val tracksState by lazy {
        _tracksState.asStateFlow()
    }

    override fun setTracks(tracks: ImmutableList<com.paranid5.crescendo.core.common.tracks.Track>) =
        _tracksState.update { tracks }

    private val _filteredTracksState: MutableStateFlow<ImmutableList<com.paranid5.crescendo.core.common.tracks.Track>> by lazy {
        MutableStateFlow(persistentListOf())
    }

    override val filteredTracksState by lazy {
        _filteredTracksState.asStateFlow()
    }

    override fun setFilteredTracks(tracks: ImmutableList<com.paranid5.crescendo.core.common.tracks.Track>) =
        _filteredTracksState.update { tracks }
}