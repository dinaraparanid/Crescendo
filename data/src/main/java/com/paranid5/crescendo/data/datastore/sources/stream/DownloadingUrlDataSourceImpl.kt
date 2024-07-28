package com.paranid5.crescendo.data.datastore.sources.stream

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.downloadingUrlFlow
import com.paranid5.crescendo.data.properties.storeDownloadingUrl
import com.paranid5.crescendo.domain.sources.stream.DownloadingUrlPublisher
import com.paranid5.crescendo.domain.sources.stream.DownloadingUrlSubscriber

class DownloadingUrlSubscriberImpl(private val dataStoreProvider: DataStoreProvider) :
    DownloadingUrlSubscriber {
    override val downloadingUrlFlow by lazy {
        dataStoreProvider.downloadingUrlFlow
    }
}

class DownloadingUrlPublisherImpl(private val dataStoreProvider: DataStoreProvider) :
    DownloadingUrlPublisher {
    override suspend fun setDownloadingUrl(url: String) =
        dataStoreProvider.storeDownloadingUrl(url)
}