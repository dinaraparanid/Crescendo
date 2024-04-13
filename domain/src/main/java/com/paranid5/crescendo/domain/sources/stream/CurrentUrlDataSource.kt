package com.paranid5.crescendo.domain.sources.stream

import kotlinx.coroutines.flow.Flow

interface CurrentUrlSubscriber {
    val currentUrlFlow: Flow<String>
}

interface CurrentUrlPublisher {
    suspend fun setCurrentUrl(url: String)
}