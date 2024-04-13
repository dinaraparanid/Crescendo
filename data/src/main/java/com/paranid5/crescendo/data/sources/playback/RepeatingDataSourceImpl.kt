package com.paranid5.crescendo.data.sources.playback

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.isRepeatingFlow
import com.paranid5.crescendo.data.properties.storeRepeating
import com.paranid5.crescendo.domain.sources.playback.RepeatingPublisher
import com.paranid5.crescendo.domain.sources.playback.RepeatingSubscriber

class RepeatingSubscriberImpl(private val storageRepository: StorageRepository) :
    RepeatingSubscriber {
    override val isRepeatingFlow by lazy {
        storageRepository.isRepeatingFlow
    }
}

class RepeatingPublisherImpl(private val storageRepository: StorageRepository) :
    RepeatingPublisher {
    override suspend fun setRepeating(isRepeating: Boolean) =
        storageRepository.storeRepeating(isRepeating)
}