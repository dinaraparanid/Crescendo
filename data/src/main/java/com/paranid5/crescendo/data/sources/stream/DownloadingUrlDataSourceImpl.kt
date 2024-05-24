package com.paranid5.crescendo.data.sources.stream

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.downloadingUrlFlow
import com.paranid5.crescendo.data.properties.storeDownloadingUrl
import com.paranid5.crescendo.domain.sources.stream.DownloadingUrlPublisher
import com.paranid5.crescendo.domain.sources.stream.DownloadingUrlSubscriber

class DownloadingUrlSubscriberImpl(private val storageRepository: StorageRepository) :
    DownloadingUrlSubscriber {
    override val downloadingUrlFlow by lazy {
        storageRepository.downloadingUrlFlow
    }
}

class DownloadingUrlPublisherImpl(private val storageRepository: StorageRepository) :
    DownloadingUrlPublisher {
    override suspend fun setDownloadingUrl(url: String) =
        storageRepository.storeDownloadingUrl(url)
}