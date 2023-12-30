package com.paranid5.crescendo.data.states.stream

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.storeCurrentUrl
import kotlinx.coroutines.flow.Flow

interface CurrentUrlStateSubscriber {
    val currentUrlFlow: Flow<String>
}

interface CurrentUrlStatePublisher {
    suspend fun setCurrentUrl(url: String)
}

class CurrentUrlStateSubscriberImpl(private val storageHandler: StorageHandler) :
    CurrentUrlStateSubscriber {
    override val currentUrlFlow by lazy {
        storageHandler.currentUrlState
    }
}

class CurrentUrlStatePublisherImpl(private val storageHandler: StorageHandler) :
    CurrentUrlStatePublisher {
    override suspend fun setCurrentUrl(url: String) =
        storageHandler.storeCurrentUrl(url)
}