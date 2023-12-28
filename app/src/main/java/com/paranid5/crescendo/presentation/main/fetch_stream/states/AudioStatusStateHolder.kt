package com.paranid5.crescendo.presentation.main.fetch_stream.states

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.storeAudioStatus
import com.paranid5.crescendo.domain.media.AudioStatus

class AudioStatusStateHolder(private val storageHandler: StorageHandler) {
    suspend fun resetAudioStatusToStreaming() =
        storageHandler.storeAudioStatus(AudioStatus.STREAMING)
}