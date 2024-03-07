package com.paranid5.crescendo.services.video_cache_service.cache

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.domain.caching.CachingStatus
import com.paranid5.crescendo.domain.caching.DownloadingStatus
import com.paranid5.crescendo.domain.caching.VideoCacheData
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService
import com.paranid5.crescendo.services.video_cache_service.extractor.extractMediaFilesAndStartCaching
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot

private const val TAG = "CacheEventLoop"

suspend fun VideoCacheService.startCacheEventLoop() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        videoQueueManager.videoQueueFlow
            .distinctUntilChanged()
            .filterNot { _ ->
                val downloadSt = mediaFileDownloader.downloadStatusState.value
                val cacheSt = cacheManager.cachingStatusState.value
                downloadSt == DownloadingStatus.CanceledAll || cacheSt == CachingStatus.CANCELED_ALL
            }
            .collect { video -> onCaching(video) }
    }

private suspend inline fun VideoCacheService.onCaching(videoData: VideoCacheData) {
    mediaFileDownloader.prepareForNewVideo()
    cacheManager.prepareForNewVideo()

    reportCachingResult(
        extractMediaFilesAndStartCaching(
            ytUrl = videoData.url,
            desiredFilename = videoData.desiredFilename,
            format = videoData.format,
            trimRange = videoData.trimRange
        )
    )
}