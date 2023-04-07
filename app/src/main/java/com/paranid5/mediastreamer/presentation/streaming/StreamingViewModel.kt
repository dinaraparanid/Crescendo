package com.paranid5.mediastreamer.presentation.streaming

import com.paranid5.mediastreamer.presentation.ObservableViewModel
import org.koin.core.component.inject

class StreamingViewModel : ObservableViewModel<StreamingPresenter, StreamingUIHandler>() {
    override val handler by inject<StreamingUIHandler>()
    override val presenter by inject<StreamingPresenter>()
}