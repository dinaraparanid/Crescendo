package com.paranid5.crescendo.data.files

import arrow.core.Either
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.data.files.VideoFiles.toAudioFile
import com.paranid5.crescendo.domain.files.MediaFilesRepository
import com.paranid5.crescendo.domain.files.model.Filename
import com.paranid5.crescendo.domain.files.model.Formats
import com.paranid5.crescendo.domain.files.model.MediaDirectory
import com.paranid5.crescendo.domain.files.model.MediaFile
import com.paranid5.crescendo.domain.files.model.MediaFileExtension

internal class MediaFilesRepositoryImpl : MediaFilesRepository {
    override suspend fun createAudioFile(
        filename: Filename,
        ext: MediaFileExtension,
        mediaDirectory: MediaDirectory,
    ): Either<Throwable, MediaFile.AudioFile> = AudioFiles.createAudioFileCatching(
        filename = filename,
        ext = ext,
        mediaDirectory = mediaDirectory,
    )

    override suspend fun createVideoFileCatching(
        filename: Filename,
        ext: MediaFileExtension,
        mediaDirectory: MediaDirectory,
    ): Either<Throwable, MediaFile.VideoFile> = VideoFiles.createVideoFileCatching(
        filename = filename,
        ext = ext,
        mediaDirectory = mediaDirectory,
    )

    override suspend fun convertVideoFileToAudio(
        file: MediaFile.VideoFile,
        audioFormat: Formats,
        trimRange: TrimRange,
    ): MediaFile.AudioFile? = file.toAudioFile(audioFormat = audioFormat, trimRange = trimRange)
}
