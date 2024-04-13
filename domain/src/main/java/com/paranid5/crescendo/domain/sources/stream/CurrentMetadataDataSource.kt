package com.paranid5.crescendo.domain.sources.stream

import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CurrentMetadataSubscriber {
    val currentMetadataFlow: Flow<VideoMetadata?>
}

interface CurrentMetadataPublisher {
    suspend fun setCurrentMetadata(metadata: VideoMetadata?)
}

inline val CurrentMetadataSubscriber.currentMetadataDurationMillisFlow
    get() = currentMetadataFlow.map { it?.durationMillis ?: 0 }