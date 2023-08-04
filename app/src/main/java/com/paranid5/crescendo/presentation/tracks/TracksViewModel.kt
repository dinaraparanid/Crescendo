package com.paranid5.crescendo.presentation.tracks

import androidx.lifecycle.SavedStateHandle
import com.googlecode.mp4parser.authoring.Track
import com.paranid5.crescendo.presentation.ObservableViewModel
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class TracksViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<TracksPresenter, TracksUIHandler>() {
    private companion object {
        private const val TRACKS = "tracks"
        private const val QUERY = "query"
    }

    override val presenter by inject<TracksPresenter> {
        val trackSavedByStateHandle = savedStateHandle
            .getStateFlow<List<Track>>(TRACKS, listOf())
            .value

        val querySavedByStateHandle = savedStateHandle
            .getStateFlow<String?>(QUERY, null)
            .value

        parametersOf(trackSavedByStateHandle, querySavedByStateHandle)
    }

    override val handler by inject<TracksUIHandler>()
}