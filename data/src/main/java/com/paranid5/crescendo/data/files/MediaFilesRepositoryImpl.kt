package com.paranid5.crescendo.data.files

import arrow.core.Either
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.data.files.VideoFiles.toAudioFile
import com.paranid5.crescendo.domain.files.MediaFilesRepository
import com.paranid5.crescendo.domain.files.entity.Filename
import com.paranid5.crescendo.domain.files.entity.Formats
import com.paranid5.crescendo.domain.files.entity.MediaDirectory
import com.paranid5.crescendo.domain.files.entity.MediaFile
import com.paranid5.crescendo.domain.files.entity.MediaFileExtension

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

    override suspend fun createVideoFile(
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

    override fun getInitialVideoDirectory(isAudio: Boolean): MediaDirectory =
        MediaDirectoryPath.getInitialVideoDirectory(isAudio = isAudio)
}
