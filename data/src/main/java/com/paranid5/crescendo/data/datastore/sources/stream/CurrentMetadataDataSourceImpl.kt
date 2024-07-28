package com.paranid5.crescendo.data.datastore.sources.stream

import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.currentMetadataFlow
import com.paranid5.crescendo.data.properties.storeCurrentMetadata
import com.paranid5.crescendo.domain.sources.stream.CurrentMetadataPublisher
import com.paranid5.crescendo.domain.sources.stream.CurrentMetadataSubscriber

class CurrentMetadataSubscriberImpl(private val dataStoreProvider: DataStoreProvider) :
    CurrentMetadataSubscriber {
    override val currentMetadataFlow by lazy {
        dataStoreProvider.currentMetadataFlow
    }
}

class CurrentMetadataPublisherImpl(private val dataStoreProvider: DataStoreProvider) :
    CurrentMetadataPublisher {
    override suspend fun setCurrentMetadata(metadata: VideoMetadata?) =
        dataStoreProvider.storeCurrentMetadata(metadata)
}