package com.paranid5.crescendo.data.files

import arrow.core.Either
import com.paranid5.crescendo.data.files.Files.createFileCatching
import com.paranid5.crescendo.domain.files.entity.Filename
import com.paranid5.crescendo.domain.files.entity.MediaDirectory
import com.paranid5.crescendo.domain.files.entity.MediaFile
import com.paranid5.crescendo.domain.files.entity.MediaFileExtension

internal object AudioFiles {
    suspend fun createAudioFileCatching(
        mediaDirectory: MediaDirectory,
        filename: Filename,
        ext: MediaFileExtension,
    ): Either<Throwable, MediaFile.AudioFile> =
        createFileCatching(mediaDirectory, filename, ext).map(MediaFile::AudioFile)
}
