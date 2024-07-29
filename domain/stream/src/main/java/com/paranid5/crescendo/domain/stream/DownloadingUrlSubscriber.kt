package com.paranid5.crescendo.domain.stream

import kotlinx.coroutines.flow.Flow

interface DownloadingUrlSubscriber {
    val downloadingUrlFlow: Flow<String>
}
