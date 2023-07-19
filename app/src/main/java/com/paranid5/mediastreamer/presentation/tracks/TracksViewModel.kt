package com.paranid5.mediastreamer.presentation.tracks

import androidx.lifecycle.SavedStateHandle
import com.paranid5.mediastreamer.presentation.ObservableViewModel
import org.koin.core.component.inject

class TracksViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<TracksPresenter, TracksUIHandler>() {
    override val presenter by inject<TracksPresenter>()
    override val handler by inject<TracksUIHandler>()
}