package com.paranid5.crescendo.data.sources.playback

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.audioStatusFlow
import com.paranid5.crescendo.data.properties.storeAudioStatus
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.playback.AudioStatusSubscriber

class AudioStatusSubscriberImpl(private val storageRepository: StorageRepository) :
    AudioStatusSubscriber {
    override val audioStatusFlow by lazy {
        storageRepository.audioStatusFlow
    }
}

class AudioStatusPublisherImpl(private val storageRepository: StorageRepository) :
    AudioStatusPublisher {
    override suspend fun setAudioStatus(audioStatus: AudioStatus) =
        storageRepository.storeAudioStatus(audioStatus)
}