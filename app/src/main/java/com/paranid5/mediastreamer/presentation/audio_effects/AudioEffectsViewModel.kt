package com.paranid5.mediastreamer.presentation.audio_effects

import androidx.lifecycle.SavedStateHandle
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.ObservableViewModel
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class AudioEffectsViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<AudioEffectsPresenter, AudioEffectsUIHandler>() {
    private companion object {
        private const val PITCH_TEXT = "pitch_text"
        private const val SPEED_TEXT = "speed_text"
    }

    private val storageHandler by inject<StorageHandler>()

    override val presenter by inject<AudioEffectsPresenter> {
        val savedByStateHandlerPitchText = savedStateHandle
            .getStateFlow<String?>(PITCH_TEXT, null)
            .value

        val savedByStorageHandlerPitch = storageHandler.pitchState.value

        val savedByStateHandlerSpeedText = savedStateHandle
            .getStateFlow<String?>(SPEED_TEXT, null)
            .value

        val savedByStorageHandlerSpeed = storageHandler.speedState.value

        parametersOf(
            savedByStateHandlerPitchText ?: savedByStorageHandlerPitch.toString(),
            savedByStateHandlerSpeedText ?: savedByStorageHandlerSpeed.toString()
        )
    }

    override val handler by inject<AudioEffectsUIHandler>()
}