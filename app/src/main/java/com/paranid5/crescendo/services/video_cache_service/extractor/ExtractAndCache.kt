package com.paranid5.crescendo.services.video_cache_service.extractor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.core.media.caching.CachingResult
import com.paranid5.crescendo.core.media.caching.cachingResult
import com.paranid5.crescendo.core.media.caching.isNotError
import com.paranid5.crescendo.core.media.caching.onCanceled
import com.paranid5.crescendo.core.media.files.MediaFile
import com.paranid5.crescendo.core.media.convertToAudioFileAndSetTagsAsync
import com.paranid5.crescendo.core.media.mergeToMP4AndSetTagsAsync
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService
import com.paranid5.crescendo.services.video_cache_service.files.initMediaFile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

private const val RETRY_DELAY = 2000L

suspend fun VideoCacheService.extractMediaFilesAndStartCaching(
    ytUrl: String,
    desiredFilename: String,
    format: Formats,
    trimRange: TrimRange
): CachingResult {
    suspend fun impl() = either {
        val (urls, metadata) = urlExtractor.extractUrlsWithMeta(
            context = this@extractMediaFilesAndStartCaching,
            ytUrl = ytUrl,
            format = format
        ).bind()

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

    fun implFlow() = flow {
        while (true) emit(impl())
    }

    return implFlow()
        .first { it.isNotErrorOrDelay() }
        .getOrNull()!!
}

private suspend fun VideoCacheService.cacheAudioFile(
    desiredFilename: String,
    audioUrl: String,
    videoMetadata: VideoMetadata,
    audioFormat: Formats,
    trimRange: TrimRange
): CachingResult {
    suspend fun impl() = cachingResult {
        val result = mediaFileDownloader.downloadAudioFile(
            desiredFilename = desiredFilename,
            mediaUrl = audioUrl,
            isAudio = true
        ).bind()

        val file = result.first() as MediaFile.VideoFile
        cacheManager.onConversionStarted()

        val audioConversionResult =
            file.convertToAudioFileAndSetTagsAsync(
                context = this@cacheAudioFile,
                videoMetadata = videoMetadata,
                audioFormat = audioFormat,
                trimRange = trimRange
            ).await()

        ensureNotNull(audioConversionResult) {
            CachingResult.ConversionError
        }

        cacheManager.onConverted()
        videoQueueManager.decrementQueueLen()
        audioConversionResult
    }

    return impl().onCanceled { videoQueueManager.decrementQueueLen() }
}

private suspend fun VideoCacheService.cacheVideoFile(
    desiredFilename: String,
    audioUrl: String,
    videoUrl: String,
    videoMetadata: VideoMetadata,
): CachingResult {
    suspend fun merge(result: CachingResult.DownloadResult) =
        cachingResult {
            val (audioFileStore, videoFileStore) = result.bind()

            val mergedMp4 = mergeToMp4(
                desiredFilename = desiredFilename,
                audioFileStore = audioFileStore,
                videoFileStore = videoFileStore,
                videoMetadata = videoMetadata
            ).bind().first()

            cacheManager.onConverted()
            videoQueueManager.decrementQueueLen()
            mergedMp4
        }

    val result = mediaFileDownloader.downloadAudioAndVideoFiles(
        desiredFilename = desiredFilename,
        audioUrl = audioUrl,
        videoUrl = videoUrl
    )

    return merge(result).onCanceled { videoQueueManager.decrementQueueLen() }
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

private suspend inline fun Either<Throwable, CachingResult?>.isNotErrorOrDelay() =
    isRight { it?.isNotError == true }.also { if (!it) delay(RETRY_DELAY) }