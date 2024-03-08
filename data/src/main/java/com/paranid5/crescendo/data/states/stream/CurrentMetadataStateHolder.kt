package com.paranid5.crescendo.data.states.stream

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentMetadataFlow
import com.paranid5.crescendo.data.properties.storeCurrentMetadata
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CurrentMetadataStateSubscriber {
    val currentMetadataFlow: Flow<com.paranid5.crescendo.core.common.metadata.VideoMetadata?>
}

interface CurrentMetadataStatePublisher {
    suspend fun setCurrentMetadata(metadata: com.paranid5.crescendo.core.common.metadata.VideoMetadata?)
}

class CurrentMetadataStateSubscriberImpl(private val storageHandler: StorageHandler) :
    CurrentMetadataStateSubscriber {
    override val currentMetadataFlow by lazy {
        storageHandler.currentMetadataFlow
    }
}

class CurrentMetadataStatePublisherImpl(private val storageHandler: StorageHandler) :
    CurrentMetadataStatePublisher {
    override suspend fun setCurrentMetadata(metadata: com.paranid5.crescendo.core.common.metadata.VideoMetadata?) =
        storageHandler.storeCurrentMetadata(metadata)
}

inline val CurrentMetadataStateSubscriber.currentMetadataDurationMillisFlow
    get() = currentMetadataFlow.map { it?.durationMillis ?: 0 }