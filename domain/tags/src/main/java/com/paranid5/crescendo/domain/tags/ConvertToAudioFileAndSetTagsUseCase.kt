package com.paranid5.crescendo.domain.tags

import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.domain.files.MediaFilesRepository
import com.paranid5.crescendo.domain.files.entity.Formats
import com.paranid5.crescendo.domain.files.entity.MediaFile
import com.paranid5.crescendo.domain.metadata.model.AudioMetadata
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConvertToAudioFileAndSetTagsUseCase(
    private val mediaFilesRepository: MediaFilesRepository,
    private val tagsRepository: TagsRepository,
) {

    suspend operator fun invoke(
        videoFile: MediaFile.VideoFile,
        videoMetadata: VideoMetadata,
        audioFormat: Formats,
        trimRange: TrimRange,
    ): MediaFile.AudioFile? = withContext(Dispatchers.IO) {
        val audioFile = mediaFilesRepository.convertVideoFileToAudio(
            file = videoFile,
            audioFormat = audioFormat,
            trimRange = trimRange,
        ) ?: return@withContext null

        tagsRepository.setAudioTags(
            audioFile = audioFile,
            metadata = AudioMetadata(
                title = videoMetadata.title,
                author = videoMetadata.author,
                covers = videoMetadata.covers,
                durationMillis = videoMetadata.durationMillis,
            ),
            audioFormat = audioFormat,
        )

        audioFile
    }
}
