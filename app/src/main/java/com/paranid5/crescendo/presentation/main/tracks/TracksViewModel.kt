package com.paranid5.crescendo.presentation.main.tracks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.domain.tracks.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

class TracksViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private companion object {
        private const val QUERY = "query"
    }

    private val _tracksState by lazy {
        MutableStateFlow(listOf<Track>())
    }

    val tracksState = _tracksState.asStateFlow()

    fun setTracks(tracks: List<Track>) {
        _tracksState.updateAndGet { tracks }
    }

    private val _queryState by lazy {
        MutableStateFlow(savedStateHandle.get<String>(QUERY))
    }

    val queryState = _queryState.asStateFlow()

    fun setQueryState(query: String?) {
        savedStateHandle[QUERY] = _queryState.updateAndGet { query }
    }
}