package com.paranid5.crescendo.data.sources.stream

import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.currentMetadataFlow
import com.paranid5.crescendo.data.properties.storeCurrentMetadata
import com.paranid5.crescendo.domain.sources.stream.CurrentMetadataPublisher
import com.paranid5.crescendo.domain.sources.stream.CurrentMetadataSubscriber

class CurrentMetadataSubscriberImpl(private val storageRepository: StorageRepository) :
    CurrentMetadataSubscriber {
    override val currentMetadataFlow by lazy {
        storageRepository.currentMetadataFlow
    }
}

class CurrentMetadataPublisherImpl(private val storageRepository: StorageRepository) :
    CurrentMetadataPublisher {
    override suspend fun setCurrentMetadata(metadata: VideoMetadata?) =
        storageRepository.storeCurrentMetadata(metadata)
}