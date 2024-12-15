package com.paranid5.crescendo.domain.stream

import com.paranid5.crescendo.domain.metadata.model.VideoMetadata

interface CurrentMetadataPublisher {
    suspend fun updateCurrentMetadata(metadata: VideoMetadata?)
}
