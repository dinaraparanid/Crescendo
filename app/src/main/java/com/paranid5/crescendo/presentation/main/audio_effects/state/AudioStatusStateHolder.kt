package com.paranid5.crescendo.presentation.main.audio_effects.state

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.audioStatusFlow

class AudioStatusStateHolder(private val storageHandler: StorageHandler) {
    val audioStatusFlow by lazy {
        storageHandler.audioStatusFlow
    }
}