package com.paranid5.crescendo.domain.stream

import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CurrentMetadataSubscriber {
    val currentMetadataFlow: Flow<VideoMetadata?>
}

inline val CurrentMetadataSubscriber.currentMetadataDurationMillisFlow
    get() = currentMetadataFlow.map { it?.durationMillis ?: 0 }
