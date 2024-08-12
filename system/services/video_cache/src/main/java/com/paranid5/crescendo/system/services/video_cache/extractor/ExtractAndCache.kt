package com.paranid5.crescendo.system.services.video_cache.extractor

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
import com.paranid5.crescendo.core.media.convertToAudioFileAndSetTagsAsync
import com.paranid5.crescendo.core.media.files.MediaFile
import com.paranid5.crescendo.core.media.tags.setVideoTagsAsync
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService
import com.paranid5.crescendo.system.services.video_cache.files.initMediaFile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.concurrent.atomic.AtomicLong

private const val RETRY_DELAY = 2000L

internal suspend fun VideoCacheService.extractMediaFilesAndStartCaching(
    ytUrl: String,
    desiredFilename: String,
    format: Formats,
    trimRange: TrimRange,
) = cachingResult {
    val cacheFile = initMediaFile(
        desiredFilename = desiredFilename,
        isAudio = false,
    ).bind()

    val progress = AtomicLong()

    suspend fun impl() = either {
        val (mediaUrl, metadata) = urlExtractor.extractUrlsWithMeta(
            context = this@extractMediaFilesAndStartCaching,
            ytUrl = ytUrl,
        ).bind()

        videoQueueManager.resetVideoMetadata(metadata)

        when (format) {
            Formats.MP4 -> cacheVideoFile(
                cacheFile = cacheFile,
                mediaUrl = mediaUrl,
                videoMetadata = metadata,
                progress = progress,
            )

            else -> cacheAudioFile(
                cacheFile = cacheFile,
                audioUrl = mediaUrl,
                videoMetadata = metadata,
                audioFormat = format,
                trimRange = trimRange,
                progress = progress,
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
    cacheFile: MediaFile,
    audioUrl: String,
    videoMetadata: VideoMetadata,
    audioFormat: Formats,
    trimRange: TrimRange,
    progress: AtomicLong,
) = cachingResult {
    suspend fun impl() = cachingResult {
        val result = mediaFileDownloader.downloadFile(
            cacheFile = cacheFile,
            mediaUrl = audioUrl,
            progress = progress,
        ).bind()

        val file = result as MediaFile.VideoFile
        cacheManager.onConversionStarted()

        val audioConversionResult =
            file.convertToAudioFileAndSetTagsAsync(
                context = this@cacheAudioFile,
                videoMetadata = videoMetadata,
                audioFormat = audioFormat,
                trimRange = trimRange,
            ).await()

        ensureNotNull(audioConversionResult) {
            CachingResult.ConversionError
        }

        cacheManager.onConverted()
        videoQueueManager.decrementQueueLen()
        audioConversionResult
    }

    impl()
        .onCanceled { videoQueueManager.decrementQueueLen() }
        .bind()
}

private suspend fun VideoCacheService.cacheVideoFile(
    cacheFile: MediaFile,
    mediaUrl: String,
    videoMetadata: VideoMetadata,
    progress: AtomicLong,
) = cachingResult {
    suspend fun impl() = cachingResult {
        val result = mediaFileDownloader.downloadFile(
            cacheFile = cacheFile,
            mediaUrl = mediaUrl,
            progress = progress,
        ).bind()

        val videoFile = result as MediaFile.VideoFile

        setVideoTagsAsync(
            context = this@cacheVideoFile,
            videoFile = videoFile,
            metadata = videoMetadata,
        ).join()

        cacheManager.onConverted()
        videoQueueManager.decrementQueueLen()
        videoFile
    }

    return impl().onCanceled { videoQueueManager.decrementQueueLen() }
}

private suspend inline fun Either<Throwable, CachingResult?>.isNotErrorOrDelay() =
    isRight { it?.isNotError == true }.also { isNotError ->
        if (isNotError.not()) delay(RETRY_DELAY)
    }
