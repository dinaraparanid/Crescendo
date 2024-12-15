package com.paranid5.crescendo.data.stream

import com.paranid5.crescendo.data.datastore.StreamDataStore
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import com.paranid5.crescendo.domain.stream.CurrentMetadataPublisher

internal class CurrentMetadataPublisherImpl(
    private val streamDataStore: StreamDataStore,
) : CurrentMetadataPublisher {
    override suspend fun updateCurrentMetadata(metadata: VideoMetadata?) =
        streamDataStore.storeCurrentMetadata(metadata)
}
