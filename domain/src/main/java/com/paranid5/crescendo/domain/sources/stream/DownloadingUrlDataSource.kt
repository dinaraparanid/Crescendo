package com.paranid5.crescendo.domain.sources.stream

import kotlinx.coroutines.flow.Flow

interface DownloadingUrlSubscriber {
    val downloadingUrlFlow: Flow<String>
}

interface DownloadingUrlPublisher {
    suspend fun setDownloadingUrl(url: String)
}
