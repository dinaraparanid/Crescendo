package com.paranid5.crescendo.cache.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.cache.data.CacheDialogDataSource
import com.paranid5.crescendo.cache.data.CacheDialogDataSourceImpl
import com.paranid5.crescendo.domain.stream.DownloadingUrlPublisher
import com.paranid5.crescendo.domain.stream.StreamRepository

internal class CacheViewModel(streamRepository: StreamRepository) : ViewModel(),
    DownloadingUrlPublisher by streamRepository,
    CacheDialogDataSource by CacheDialogDataSourceImpl(streamRepository)
