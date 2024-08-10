package com.paranid5.crescendo.system.services.video_cache.cache

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.core.common.caching.CachingStatus
import com.paranid5.crescendo.core.common.caching.DownloadingStatus
import com.paranid5.crescendo.core.common.caching.VideoCacheData
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService
import com.paranid5.crescendo.system.services.video_cache.extractor.extractMediaFilesAndStartCaching
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot

internal suspend inline fun VideoCacheService.startCacheEventLoop() =
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
            trimRange = videoData.trimRange,
        )
    )
}