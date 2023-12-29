package com.paranid5.crescendo.presentation.main.playing.states

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.audioStatusFlow
import com.paranid5.crescendo.data.properties.storeAudioStatus
import com.paranid5.crescendo.domain.media.AudioStatus

class AudioStatusStateHolder(private val storageHandler: StorageHandler) {
    val audioStatusFlow by lazy {
        storageHandler.audioStatusFlow
    }

    suspend fun setAudioStatus(audioStatus: AudioStatus) =
        storageHandler.storeAudioStatus(audioStatus)
}