package com.paranid5.crescendo.services.video_cache_service.cache

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.domain.caching.CachingStatus
import com.paranid5.crescendo.domain.caching.DownloadingStatus
import com.paranid5.crescendo.domain.caching.VideoCacheData
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService
import com.paranid5.crescendo.services.video_cache_service.extractor.extractMediaFilesAndStartCaching
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onEach

private const val TAG = "CacheEventLoop"

suspend fun VideoCacheService.startCacheEventLoop() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        cacheManager.videoCacheFlow
            .onEach { println(it) }
            .distinctUntilChanged()
            .filterNot { _ ->
                val downloadSt = mediaFileDownloader.downloadStatusState.value
                val cacheSt = cacheManager.cachingStatusState.value
                Log.d(TAG, "Download: $downloadSt; Cache: $cacheSt")
                downloadSt == DownloadingStatus.CANCELED_ALL || cacheSt == CachingStatus.CANCELED_ALL
            }
            .collect { video -> onCaching(video) }
    }

private suspend inline fun VideoCacheService.onCaching(videoData: VideoCacheData) {
    mediaFileDownloader.prepareForNewVideo()
    cacheManager.prepareForNewVideo()

    extractMediaFilesAndStartCaching(
        ytUrl = videoData.url,
        desiredFilename = videoData.desiredFilename,
        format = videoData.format,
        trimRange = videoData.trimRange
    ).getOrNull()?.let(this::reportCachingResult)

    cacheManager.decrementQueueLen()
}