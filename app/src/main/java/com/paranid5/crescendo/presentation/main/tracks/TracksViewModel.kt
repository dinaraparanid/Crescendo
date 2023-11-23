package com.paranid5.crescendo.presentation.main.tracks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.tracks.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

class TracksViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private companion object {
        private const val TRACKS = "tracks"
        private const val QUERY = "query"
    }

    private val _tracksState by lazy {
        MutableStateFlow(savedStateHandle[TRACKS] ?: listOf<Track>())
    }

    val tracksState = _tracksState.asStateFlow()

    fun setTracks(tracks: List<Track>) {
        savedStateHandle[TRACKS] = _tracksState.updateAndGet { tracks }
    }

    private val _queryState by lazy {
        MutableStateFlow(savedStateHandle.get<String>(QUERY))
    }

    val queryState = _queryState.asStateFlow()

    fun setQueryState(query: String?) {
        savedStateHandle[QUERY] = _queryState.updateAndGet { query }
    }
}