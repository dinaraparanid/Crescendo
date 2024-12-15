package com.paranid5.crescendo.domain.stream

import arrow.core.Either
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata

interface VideoMetadataApi {
    suspend fun getVideoMetadata(url: String): Either<Throwable, VideoMetadata>
}
