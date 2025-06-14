package com.paranid5.crescendo.system.services.video_cache.extractor

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import com.paranid5.crescendo.caching.entity.CachingResult
import com.paranid5.crescendo.caching.entity.cachingResult
import com.paranid5.crescendo.caching.entity.isError
import com.paranid5.crescendo.caching.entity.onCanceled
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.domain.files.entity.Formats
import com.paranid5.crescendo.domain.files.entity.MediaFile
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService
import com.paranid5.crescendo.utils.extensions.notNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.atomic.AtomicLong

private const val RETRY_DELAY = 2000L

internal suspend fun VideoCacheService.extractMediaFilesAndStartCaching(
    ytUrl: String,
    desiredFilename: String,
    format: Formats,
    trimRange: TrimRange,
) = cachingResult {
    val cacheFile = initMediaFileUseCase(
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
        .onEach { if (it.isNotError.not()) delay(RETRY_DELAY) }
        .first { it.isNotError }
        .getOrNull()
        .notNull
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

        val audioConversionResult = convertToAudioFileAndSetTagsUseCase(
            videoFile = file,
            videoMetadata = videoMetadata,
            audioFormat = audioFormat,
            trimRange = trimRange,
        )

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

        tagsRepository.setVideoTags(
            videoFile = videoFile,
            metadata = videoMetadata,
        )

        cacheManager.onConverted()
        videoQueueManager.decrementQueueLen()
        videoFile
    }

    return impl().onCanceled { videoQueueManager.decrementQueueLen() }
}

private inline val Either<Throwable, CachingResult?>.isNotError
    get() = isRight { it?.isError?.not() == true }
