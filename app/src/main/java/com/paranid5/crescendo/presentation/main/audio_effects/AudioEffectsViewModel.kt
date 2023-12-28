package com.paranid5.crescendo.presentation.main.audio_effects

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.presentation.main.audio_effects.state.AudioStatusStateHolder
import com.paranid5.crescendo.presentation.main.audio_effects.state.EffectsStateHolder

class AudioEffectsViewModel(private val storageHandler: StorageHandler) : ViewModel() {
    val audioStatusStateHolder by lazy {
        AudioStatusStateHolder(storageHandler)
    }

    val effectsStateHolder by lazy {
        EffectsStateHolder(storageHandler)
    }
}