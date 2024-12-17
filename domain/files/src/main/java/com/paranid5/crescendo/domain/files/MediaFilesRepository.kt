package com.paranid5.crescendo.domain.files

import android.os.Environment
import arrow.core.Either
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.domain.files.model.Filename
import com.paranid5.crescendo.domain.files.model.Formats
import com.paranid5.crescendo.domain.files.model.MediaDirectory
import com.paranid5.crescendo.domain.files.model.MediaFile
import com.paranid5.crescendo.domain.files.model.MediaFileExtension

interface MediaFilesRepository {
    suspend fun createAudioFile(
        filename: Filename,
        ext: MediaFileExtension,
        mediaDirectory: MediaDirectory = MediaDirectory(Environment.DIRECTORY_MUSIC),
    ): Either<Throwable, MediaFile.AudioFile>

    suspend fun createVideoFileCatching(
        filename: Filename,
        ext: MediaFileExtension,
        mediaDirectory: MediaDirectory = MediaDirectory(Environment.DIRECTORY_MOVIES),
    ): Either<Throwable, MediaFile.VideoFile>

    suspend fun convertVideoFileToAudio(
        file: MediaFile.VideoFile,
        audioFormat: Formats,
        trimRange: TrimRange,
    ): MediaFile.AudioFile?
}
