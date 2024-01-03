package com.paranid5.crescendo.services.video_cache_service.files

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.paranid5.crescendo.domain.caching.CachingResult
import com.paranid5.crescendo.domain.media.files.MediaFile
import com.paranid5.crescendo.domain.media.files.createVideoFileCatching
import com.paranid5.crescendo.domain.media.files.getInitialVideoDirectory

suspend fun initMediaFile(
    desiredFilename: String,
    isAudio: Boolean
) = either {
    val storeFileRes = createVideoFileCatching(
        mediaDirectory = getInitialVideoDirectory(isAudio),
        filename = desiredFilename.replace(Regex("\\W+"), "_"),
        ext = "mp4"
    )

    ensure(storeFileRes.isRight()) {
        CachingResult.DownloadResult.FileCreationError
    }

    storeFileRes.getOrNull()!!
}

suspend inline fun prepareMediaFilesForMP4Merging(
    desiredFilename: String,
): Either<CachingResult.DownloadResult.FileCreationError, Pair<MediaFile, MediaFile.VideoFile>> {
    val audioFileStoreRes = initMediaFile(
        desiredFilename,
        isAudio = true
    )

    val audioFileStore = when (audioFileStoreRes) {
        is Either.Left -> return audioFileStoreRes.value.left()
        is Either.Right -> audioFileStoreRes.value
    }

    val videoFileStoreRes = initMediaFile(
        desiredFilename,
        isAudio = false
    )

    val videoFileStore = when (videoFileStoreRes) {
        is Either.Left -> {
            audioFileStore.delete()
            return videoFileStoreRes.value.left()
        }

        is Either.Right -> videoFileStoreRes.value
    }

    return Either.Right(audioFileStore to videoFileStore)
}