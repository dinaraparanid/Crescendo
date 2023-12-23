package com.paranid5.crescendo.presentation.main.trimmer.states

import androidx.lifecycle.SavedStateHandle
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.setEndPosInMillis
import com.paranid5.crescendo.presentation.main.trimmer.properties.setStartPosInMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

class TrackStateHolder(private val savedStateHandle: SavedStateHandle) {
    private companion object {
        private const val TRACK = "track"
    }

    private val _trackState by lazy {
        MutableStateFlow(savedStateHandle.get<Track>(TRACK))
    }

    val trackState = _trackState.asStateFlow()

    fun setTrack(track: Track, viewModel: TrimmerViewModel) {
        savedStateHandle[TRACK] = _trackState.updateAndGet { track }
        viewModel.setStartPosInMillis(0)
        viewModel.setEndPosInMillis(track.durationMillis)
    }
}