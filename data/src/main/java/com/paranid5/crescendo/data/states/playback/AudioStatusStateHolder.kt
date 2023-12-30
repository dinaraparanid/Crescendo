package com.paranid5.crescendo.data.states.playback

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.audioStatusFlow
import com.paranid5.crescendo.data.properties.storeAudioStatus
import com.paranid5.crescendo.domain.media.AudioStatus
import kotlinx.coroutines.flow.Flow

interface AudioStatusStateSubscriber {
    val audioStatusFlow: Flow<AudioStatus?>
}

interface AudioStatusStatePublisher {
    suspend fun setAudioStatus(audioStatus: AudioStatus)
}

class AudioStatusStateSubscriberImpl(private val storageHandler: StorageHandler) :
    AudioStatusStateSubscriber {
    override val audioStatusFlow by lazy {
        storageHandler.audioStatusFlow
    }
}

class AudioStatusStatePublisherImpl(private val storageHandler: StorageHandler) :
    AudioStatusStatePublisher {
    override suspend fun setAudioStatus(audioStatus: AudioStatus) =
        storageHandler.storeAudioStatus(audioStatus)
}