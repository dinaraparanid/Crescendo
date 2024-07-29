package com.paranid5.crescendo.data.stream

import com.paranid5.crescendo.data.datastore.StreamDataStore
import com.paranid5.crescendo.domain.stream.DownloadingUrlPublisher

internal class DownloadingUrlPublisherImpl(
    private val streamDataStore: StreamDataStore,
) : DownloadingUrlPublisher {
    override suspend fun updateDownloadingUrl(url: String) =
        streamDataStore.storeDownloadingUrl(url)
}
