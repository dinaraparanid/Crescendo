package com.paranid5.crescendo.services.video_cache_service.extractor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.paranid5.crescendo.domain.caching.CachingResult
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.media.files.MediaFile
import com.paranid5.crescendo.domain.metadata.VideoMetadata
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.media.convertToAudioFileAndSetTagsAsync
import com.paranid5.crescendo.media.mergeToMP4AndSetTagsAsync
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService
import com.paranid5.crescendo.services.video_cache_service.files.initMediaFile
import com.paranid5.crescendo.services.video_cache_service.reportExtractionError

suspend fun VideoCacheService.extractMediaFilesAndStartCaching(
    ytUrl: String,
    desiredFilename: String,
    format: Formats,
    trimRange: TrimRange
) = either {
    val extractRes = urlExtractor.extractUrlsWithMeta(
        context = this@extractMediaFilesAndStartCaching,
        ytUrl = ytUrl,
        format = format
    )

    ensure(extractRes.isRight()) {
        reportExtractionError(extractRes.leftOrNull()!!)
    }

    val (urls, metadata) = extractRes.getOrNull()!!
    videoQueueManager.resetVideoMetadata(metadata)

    when (format) {
        Formats.MP4 -> cacheVideoFile(
            desiredFilename = desiredFilename,
            audioUrl = urls[0],
            videoUrl = urls[1],
            videoMetadata = metadata
        )

        else -> cacheAudioFile(
            desiredFilename = desiredFilename,
            audioUrl = urls[0],
            videoMetadata = metadata,
            audioFormat = format,
            trimRange = trimRange
        )
    }
}

private suspend inline fun VideoCacheService.cacheAudioFile(
    desiredFilename: String,
    audioUrl: String,
    videoMetadata: VideoMetadata,
    audioFormat: Formats,
    trimRange: TrimRange
): CachingResult {
    val result = mediaFileDownloader.downloadAudioFile(
        desiredFilename = desiredFilename,
        mediaUrl = audioUrl,
        isAudio = true
    )

    if (result is CachingResult.DownloadResult.Error) {
        onDownloadError(result)
        return result
    }

    if (result !is CachingResult.DownloadResult.Success) {
        videoQueueManager.decrementQueueLen()
        return result
    }

    val file = result.files[0] as MediaFile.VideoFile
    cacheManager.onConversionStarted()

    return when (val audioConversionResult =
        file.convertToAudioFileAndSetTagsAsync(
            context = this,
            videoMetadata = videoMetadata,
            audioFormat = audioFormat,
            trimRange = trimRange
        ).await()
    ) {
        null -> {
            cacheManager.onCachingError(file)
            videoQueueManager.decrementQueueLen()
            CachingResult.ConversionError
        }

        else -> {
            cacheManager.onConverted()
            videoQueueManager.decrementQueueLen()
            CachingResult.Success(audioConversionResult)
        }
    }
}

private suspend inline fun VideoCacheService.cacheVideoFile(
    desiredFilename: String,
    audioUrl: String,
    videoUrl: String,
    videoMetadata: VideoMetadata,
): CachingResult {
    val result = mediaFileDownloader.downloadAudioAndVideoFiles(
        desiredFilename = desiredFilename,
        audioUrl = audioUrl,
        videoUrl = videoUrl
    )

    if (result is CachingResult.DownloadResult.Error) {
        onDownloadError(result)
        return result
    }

    if (result !is CachingResult.DownloadResult.Success) {
        videoQueueManager.decrementQueueLen()
        return result
    }

    val (audioFileStore, videoFileStore) = result.files

    return mergeToMp4(
        desiredFilename = desiredFilename,
        audioFileStore = audioFileStore,
        videoFileStore = videoFileStore,
        videoMetadata = videoMetadata
    )
}

private suspend inline fun VideoCacheService.mergeToMp4(
    desiredFilename: String,
    audioFileStore: MediaFile,
    videoFileStore: MediaFile,
    videoMetadata: VideoMetadata
) = when (val storeFileRes =
    initMediaFile(desiredFilename, isAudio = false)
) {
    is Either.Left -> {
        cacheManager.onCachingError(audioFileStore, videoFileStore)
        CachingResult.DownloadResult.FileCreationError
    }

    is Either.Right -> mergeToMP4AndSetTagsAsync(
        context = this,
        audioTrack = audioFileStore,
        videoTrack = videoFileStore,
        mp4StoreFile = storeFileRes.value,
        videoMetadata = videoMetadata
    ).await().also { cacheManager.onConverted() }
}

private fun VideoCacheService.onDownloadError(result: CachingResult.DownloadResult.Error) {
    videoQueueManager.decrementQueueLen()

    mediaFileDownloader.onDownloadError(
        errorCode = result.statusCode.value,
        errorDescription = result.statusCode.description
    )
}