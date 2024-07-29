package com.paranid5.crescendo.domain.stream

import com.paranid5.crescendo.core.common.metadata.VideoMetadata

interface CurrentMetadataPublisher {
    suspend fun updateCurrentMetadata(metadata: VideoMetadata?)
}
