package com.paranid5.crescendo.presentation.audio_effects

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.domain.StorageHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import org.koin.core.component.KoinComponent

class AudioEffectsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val storageHandler: StorageHandler
) : ViewModel() {
    private companion object {
        private const val PITCH_TEXT = "pitch_text"
        private const val SPEED_TEXT = "speed_text"
    }

    private val _pitchTextState by lazy {
        val savedByStateHandlerPitchText = savedStateHandle.get<String>(PITCH_TEXT)
        val savedByStorageHandlerPitch = storageHandler.pitchState.value
        MutableStateFlow(savedByStateHandlerPitchText ?: savedByStorageHandlerPitch.toString())
    }

    private val _speedTextState by lazy {
        val savedByStateHandlerSpeedText = savedStateHandle.get<String>(SPEED_TEXT)
        val savedByStorageHandlerSpeed = storageHandler.speedState.value
        MutableStateFlow(savedByStateHandlerSpeedText ?: savedByStorageHandlerSpeed.toString())
    }

    val pitchTextState by lazy { _pitchTextState.asStateFlow() }

    fun setPitchText(pitchText: String) {
        savedStateHandle[PITCH_TEXT] = _pitchTextState.updateAndGet { pitchText }
    }

    val speedTextState by lazy { _speedTextState.asStateFlow() }

    fun setSpeedText(pitchText: String) {
        savedStateHandle[SPEED_TEXT] = _speedTextState.updateAndGet { pitchText }
    }
}