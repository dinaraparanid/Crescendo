package com.paranid5.mediastreamer.presentation.view_models

import androidx.lifecycle.SavedStateHandle
import com.paranid5.mediastreamer.presentation.presenters.StreamingPresenter
import com.paranid5.mediastreamer.presentation.ui_handlers.StreamingUIHandler
import org.koin.core.component.inject

class StreamingViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<StreamingPresenter, StreamingUIHandler>() {
    override val handler by inject<StreamingUIHandler>()
    override val presenter by inject<StreamingPresenter>()
}