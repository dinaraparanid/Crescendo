package com.paranid5.crescendo.presentation.main.trimmer.states

import com.paranid5.crescendo.core.common.tracks.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface TrackStateHolder {
    val trackState: StateFlow<com.paranid5.crescendo.core.common.tracks.Track?>
    fun setTrack(track: com.paranid5.crescendo.core.common.tracks.Track)
}

class TrackStateHolderImpl : TrackStateHolder {
    private val _trackState by lazy {
        MutableStateFlow<com.paranid5.crescendo.core.common.tracks.Track?>(null)
    }

    override val trackState by lazy {
        _trackState.asStateFlow()
    }

    override fun setTrack(track: com.paranid5.crescendo.core.common.tracks.Track) =
        _trackState.update { track }
}