package com.paranid5.crescendo.data.states.playback

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.audioStatusFlow
import com.paranid5.crescendo.data.properties.storeAudioStatus
import kotlinx.coroutines.flow.Flow

interface AudioStatusStateSubscriber {
    val audioStatusFlow: Flow<AudioStatus?>
}

interface AudioStatusStatePublisher {
    suspend fun setAudioStatus(audioStatus: AudioStatus)
}

class AudioStatusStateSubscriberImpl(private val storageRepository: StorageRepository) :
    AudioStatusStateSubscriber {
    override val audioStatusFlow by lazy {
        storageRepository.audioStatusFlow
    }
}

class AudioStatusStatePublisherImpl(private val storageRepository: StorageRepository) :
    AudioStatusStatePublisher {
    override suspend fun setAudioStatus(audioStatus: AudioStatus) =
        storageRepository.storeAudioStatus(audioStatus)
}