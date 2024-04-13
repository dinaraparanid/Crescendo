package com.paranid5.crescendo.domain.sources.playback

import kotlinx.coroutines.flow.Flow

interface RepeatingSubscriber {
    val isRepeatingFlow: Flow<Boolean>
}

interface RepeatingPublisher {
    suspend fun setRepeating(isRepeating: Boolean)
}