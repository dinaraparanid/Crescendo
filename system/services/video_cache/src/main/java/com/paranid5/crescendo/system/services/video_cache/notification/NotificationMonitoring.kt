package com.paranid5.crescendo.system.services.video_cache.notification

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Tuple6
import com.paranid5.crescendo.core.common.caching.CachingStatus
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

private const val TOO_FREQUENT_UPDATES = 1000

internal suspend inline fun VideoCacheService.startNotificationMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        combine(
            mediaFileDownloader.downloadStatusState,
            mediaFileDownloader.videoDownloadProgressState,
            cacheManager.cachingStatusState,
            videoQueueManager.currentVideoMetadataState,
            videoQueueManager.videoQueueLenState,
        ) { downloadStatus, downloadProgress, cachingStatus, meta, queueLen ->
            Tuple6(
                downloadStatus,
                downloadProgress,
                cachingStatus,
                meta,
                queueLen,
                System.currentTimeMillis(),
            )
        }.distinctUntilChanged { (_, _, _, _, _, prevTimestamp), (_, _, curCacheSt, _, _, curTimestamp) ->
            when {
                curCacheSt != CachingStatus.NONE -> false
                else -> curTimestamp - prevTimestamp < TOO_FREQUENT_UPDATES
            }
        }.collectLatest { (downloadSt, progress, cacheSt, meta, qLen) ->
            notificationManager.showNotification(
                service = this@startNotificationMonitoring,
                downloadStatus = downloadSt,
                cacheStatus = cacheSt,
                videoMetadata = meta,
                videoQueueLen = qLen,
                downloadedBytes = progress.downloadedBytes,
                totalBytes = progress.totalBytes,
            )
        }
    }