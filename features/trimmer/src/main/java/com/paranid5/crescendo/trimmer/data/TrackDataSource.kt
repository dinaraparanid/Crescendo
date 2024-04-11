package com.paranid5.crescendo.trimmer.data

import com.paranid5.crescendo.core.common.tracks.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal interface TrackDataSource {
    val trackState: StateFlow<Track?>
    fun setTrack(track: Track)
}

internal class TrackDataSourceImpl : TrackDataSource {
    private val _trackState by lazy {
        MutableStateFlow<Track?>(null)
    }

    override val trackState by lazy {
        _trackState.asStateFlow()
    }

    override fun setTrack(track: Track) =
        _trackState.update { track }
}