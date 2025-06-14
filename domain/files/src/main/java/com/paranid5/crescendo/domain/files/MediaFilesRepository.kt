package com.paranid5.crescendo.domain.files

import android.os.Environment
import arrow.core.Either
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.domain.files.entity.Filename
import com.paranid5.crescendo.domain.files.entity.Formats
import com.paranid5.crescendo.domain.files.entity.MediaDirectory
import com.paranid5.crescendo.domain.files.entity.MediaFile
import com.paranid5.crescendo.domain.files.entity.MediaFileExtension

interface MediaFilesRepository {
    suspend fun createAudioFile(
        filename: Filename,
        ext: MediaFileExtension,
        mediaDirectory: MediaDirectory = MediaDirectory(Environment.DIRECTORY_MUSIC),
    ): Either<Throwable, MediaFile.AudioFile>

    suspend fun createVideoFile(
        filename: Filename,
        ext: MediaFileExtension,
        mediaDirectory: MediaDirectory = MediaDirectory(Environment.DIRECTORY_MOVIES),
    ): Either<Throwable, MediaFile.VideoFile>

    suspend fun convertVideoFileToAudio(
        file: MediaFile.VideoFile,
        audioFormat: Formats,
        trimRange: TrimRange,
    ): MediaFile.AudioFile?

    fun getInitialVideoDirectory(isAudio: Boolean): MediaDirectory
}
