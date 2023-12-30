package com.paranid5.crescendo.presentation.main.trimmer.states

import com.paranid5.crescendo.domain.tracks.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface TrackStateHolder {
    val trackState: StateFlow<Track?>
    fun setTrack(track: Track)
}

class TrackStateHolderImpl : TrackStateHolder {
    private val _trackState by lazy {
        MutableStateFlow<Track?>(null)
    }

    override val trackState by lazy {
        _trackState.asStateFlow()
    }

    override fun setTrack(track: Track) =
        _trackState.update { track }
}