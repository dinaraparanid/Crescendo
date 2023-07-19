package com.paranid5.mediastreamer.presentation.tracks

import androidx.lifecycle.SavedStateHandle
import com.googlecode.mp4parser.authoring.Track
import com.paranid5.mediastreamer.presentation.ObservableViewModel
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class TracksViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<TracksPresenter, TracksUIHandler>() {
    private companion object {
        private const val TRACKS = "tracks"
    }

    override val presenter by inject<TracksPresenter> {
        val savedByStateHandle = savedStateHandle
            .getStateFlow<List<Track>>(TRACKS, listOf())
            .value

        parametersOf(savedByStateHandle)
    }

    override val handler by inject<TracksUIHandler>()
}