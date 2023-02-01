package com.paranid5.mediastreamer.presentation.streaming

import androidx.lifecycle.SavedStateHandle
import com.paranid5.mediastreamer.presentation.ObservableViewModel
import org.koin.core.component.inject

class StreamingViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<StreamingPresenter, StreamingUIHandler>() {
    override val handler by inject<StreamingUIHandler>()
    override val presenter by inject<StreamingPresenter>()
}