package com.paranid5.crescendo.data.sources.stream

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.currentUrlFlow
import com.paranid5.crescendo.data.properties.storeCurrentUrl
import kotlinx.coroutines.flow.Flow

interface CurrentUrlStateSubscriber {
    val currentUrlFlow: Flow<String>
}

interface CurrentUrlStatePublisher {
    suspend fun setCurrentUrl(url: String)
}

class CurrentUrlStateSubscriberImpl(private val storageRepository: StorageRepository) :
    CurrentUrlStateSubscriber {
    override val currentUrlFlow by lazy {
        storageRepository.currentUrlFlow
    }
}

class CurrentUrlStatePublisherImpl(private val storageRepository: StorageRepository) :
    CurrentUrlStatePublisher {
    override suspend fun setCurrentUrl(url: String) =
        storageRepository.storeCurrentUrl(url)
}