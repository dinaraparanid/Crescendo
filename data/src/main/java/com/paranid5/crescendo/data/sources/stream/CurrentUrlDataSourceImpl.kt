package com.paranid5.crescendo.data.sources.stream

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.currentUrlFlow
import com.paranid5.crescendo.data.properties.storeCurrentUrl
import com.paranid5.crescendo.domain.sources.stream.CurrentUrlPublisher
import com.paranid5.crescendo.domain.sources.stream.CurrentUrlSubscriber

class CurrentUrlSubscriberImpl(private val storageRepository: StorageRepository) :
    CurrentUrlSubscriber {
    override val currentUrlFlow by lazy {
        storageRepository.currentUrlFlow
    }
}

class CurrentUrlPublisherImpl(private val storageRepository: StorageRepository) :
    CurrentUrlPublisher {
    override suspend fun setCurrentUrl(url: String) =
        storageRepository.storeCurrentUrl(url)
}