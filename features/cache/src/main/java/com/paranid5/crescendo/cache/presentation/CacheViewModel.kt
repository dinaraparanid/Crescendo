package com.paranid5.crescendo.cache.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.cache.data.CacheDialogDataSource
import com.paranid5.crescendo.cache.data.CacheDialogDataSourceImpl
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.datastore.sources.stream.DownloadingUrlPublisherImpl
import com.paranid5.crescendo.domain.sources.stream.DownloadingUrlPublisher

internal class CacheViewModel(dataStoreProvider: DataStoreProvider) : ViewModel(),
    DownloadingUrlPublisher by DownloadingUrlPublisherImpl(dataStoreProvider),
    CacheDialogDataSource by CacheDialogDataSourceImpl(dataStoreProvider)