package com.paranid5.crescendo.cache.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.cache.data.CacheDialogDataSource
import com.paranid5.crescendo.cache.data.CacheDialogDataSourceImpl
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.stream.DownloadingUrlPublisherImpl
import com.paranid5.crescendo.data.sources.stream.DownloadingUrlSubscriberImpl
import com.paranid5.crescendo.domain.sources.stream.DownloadingUrlPublisher
import com.paranid5.crescendo.domain.sources.stream.DownloadingUrlSubscriber

internal class CacheViewModel(storageRepository: StorageRepository) : ViewModel(),
    DownloadingUrlPublisher by DownloadingUrlPublisherImpl(storageRepository),
    CacheDialogDataSource by CacheDialogDataSourceImpl(storageRepository)