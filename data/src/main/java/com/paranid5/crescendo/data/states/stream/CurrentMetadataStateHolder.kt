package com.paranid5.crescendo.data.states.stream

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.currentMetadataFlow
import com.paranid5.crescendo.data.properties.storeCurrentMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CurrentMetadataStateSubscriber {
    val currentMetadataFlow: Flow<com.paranid5.crescendo.core.common.metadata.VideoMetadata?>
}

interface CurrentMetadataStatePublisher {
    suspend fun setCurrentMetadata(metadata: com.paranid5.crescendo.core.common.metadata.VideoMetadata?)
}

class CurrentMetadataStateSubscriberImpl(private val storageRepository: StorageRepository) :
    CurrentMetadataStateSubscriber {
    override val currentMetadataFlow by lazy {
        storageRepository.currentMetadataFlow
    }
}

class CurrentMetadataStatePublisherImpl(private val storageRepository: StorageRepository) :
    CurrentMetadataStatePublisher {
    override suspend fun setCurrentMetadata(metadata: com.paranid5.crescendo.core.common.metadata.VideoMetadata?) =
        storageRepository.storeCurrentMetadata(metadata)
}

inline val CurrentMetadataStateSubscriber.currentMetadataDurationMillisFlow
    get() = currentMetadataFlow.map { it?.durationMillis ?: 0 }