package com.paranid5.crescendo.domain.stream

interface DownloadingUrlPublisher {
    suspend fun updateDownloadingUrl(url: String)
}
