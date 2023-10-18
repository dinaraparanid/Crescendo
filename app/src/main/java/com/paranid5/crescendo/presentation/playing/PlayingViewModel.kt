package com.paranid5.crescendo.presentation.playing

import androidx.lifecycle.SavedStateHandle
import com.paranid5.crescendo.presentation.ObservableViewModel
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class PlayingViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<PlayingPresenter, PlayingUIHandler>() {
    private companion object {
        private const val AMPLITUDES = "amplitudes"
    }

    override val presenter by inject<PlayingPresenter> {
        val savedByStateHandle = savedStateHandle
            .getStateFlow<List<Int>>(AMPLITUDES, listOf())
            .value

        parametersOf(savedByStateHandle)
    }

    override val handler by inject<PlayingUIHandler>()
}