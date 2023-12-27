package com.paranid5.crescendo.presentation.main.audio_effects

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.areAudioEffectsEnabledFlow
import com.paranid5.crescendo.data.properties.audioStatusFlow
import com.paranid5.crescendo.data.properties.bassStrengthFlow
import com.paranid5.crescendo.data.properties.pitchTextFlow
import com.paranid5.crescendo.data.properties.reverbPresetFlow
import com.paranid5.crescendo.data.properties.speedTextFlow
import com.paranid5.crescendo.data.properties.storePitch
import com.paranid5.crescendo.data.properties.storeSpeed

class AudioEffectsViewModel(private val storageHandler: StorageHandler) : ViewModel() {
    val areAudioEffectsEnabledFlow by lazy {
        storageHandler.areAudioEffectsEnabledFlow
    }

    val audioStatusFlow by lazy {
        storageHandler.audioStatusFlow
    }

    val bassStrengthFlow by lazy {
        storageHandler.bassStrengthFlow
    }

    val reverbPresetFlow by lazy {
        storageHandler.reverbPresetFlow
    }

    val pitchTextFlow by lazy {
        storageHandler.pitchTextFlow
    }

    val speedTextState by lazy {
        storageHandler.speedTextFlow
    }

    suspend fun storePitch(pitch: Float) {
        storageHandler.storePitch(pitch)
    }

    suspend fun storeSpeed(speed: Float) {
        storageHandler.storeSpeed(speed)
    }
}