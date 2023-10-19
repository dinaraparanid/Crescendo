package com.paranid5.crescendo.presentation.playing

import androidx.lifecycle.SavedStateHandle
import com.paranid5.crescendo.presentation.ObservableViewModel
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class PlayingViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<PlayingPresenter, PlayingUIHandler>() {
    private companion object {
        private const val AMPLITUDES = "amplitudes"
        private const val AUDIO_URL = "audio_url"
    }

    override val presenter by inject<PlayingPresenter> {
        val amplitudesSavedByStateHandle = savedStateHandle
            .getStateFlow<List<Int>>(AMPLITUDES, listOf())
            .value

        val audioUrlSavedByStateHandle = savedStateHandle
            .getStateFlow<String?>(AUDIO_URL, null)
            .value

        parametersOf(amplitudesSavedByStateHandle, audioUrlSavedByStateHandle)
    }

    override val handler by inject<PlayingUIHandler>()
}