package com.paranid5.crescendo.domain.tags

import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.media.files.MediaFile
import com.paranid5.crescendo.domain.metadata.model.AudioMetadata
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata

interface TagsRepository {
    suspend fun setAudioTags(
        audioFile: MediaFile.AudioFile,
        metadata: AudioMetadata,
        audioFormat: Formats,
    )

    suspend fun setVideoTags(
        videoFile: MediaFile.VideoFile,
        metadata: VideoMetadata,
    )
}
