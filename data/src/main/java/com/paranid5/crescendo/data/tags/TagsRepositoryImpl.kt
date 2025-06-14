package com.paranid5.crescendo.data.tags

import android.content.Context
import com.paranid5.crescendo.domain.files.entity.Formats
import com.paranid5.crescendo.domain.files.entity.MediaFile
import com.paranid5.crescendo.domain.metadata.model.AudioMetadata
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import com.paranid5.crescendo.domain.tags.TagsRepository

internal class TagsRepositoryImpl(private val context: Context) : TagsRepository {

    override suspend fun setAudioTags(
        audioFile: MediaFile.AudioFile,
        metadata: AudioMetadata,
        audioFormat: Formats,
    ) = AudioTags.setAudioTags(
        context = context,
        audioFile = audioFile,
        metadata = metadata,
        audioFormat = audioFormat,
    )

    override suspend fun setVideoTags(
        videoFile: MediaFile.VideoFile,
        metadata: VideoMetadata,
    ) = VideoTags.setVideoTags(
        context = context,
        videoFile = videoFile,
        metadata = metadata,
    )
}
