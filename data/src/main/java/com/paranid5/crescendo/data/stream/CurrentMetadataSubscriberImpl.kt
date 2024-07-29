package com.paranid5.crescendo.data.stream

import com.paranid5.crescendo.data.datastore.StreamDataStore
import com.paranid5.crescendo.domain.stream.CurrentMetadataSubscriber

internal class CurrentMetadataSubscriberImpl(
    streamDataStore: StreamDataStore,
) : CurrentMetadataSubscriber {
    override val currentMetadataFlow by lazy {
        streamDataStore.currentMetadataFlow
    }
}
