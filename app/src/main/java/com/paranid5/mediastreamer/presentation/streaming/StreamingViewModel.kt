package com.paranid5.mediastreamer.presentation.streaming

import androidx.lifecycle.SavedStateHandle
import com.paranid5.mediastreamer.presentation.ObservableViewModel
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class StreamingViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<StreamingPresenter, StreamingUIHandler>() {
    private companion object {
        private const val IS_PLAYING = "is_playing"
    }

    override val handler by inject<StreamingUIHandler>()

    override val presenter by inject<StreamingPresenter> {
        val savedByStateHandle = savedStateHandle
            .getStateFlow(IS_PLAYING, false)
            .value

        parametersOf(savedByStateHandle)
    }
}