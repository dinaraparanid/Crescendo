package com.paranid5.crescendo.services.video_cache_service.notification

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Tuple5
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

suspend inline fun VideoCacheService.startNotificationMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        combine(
            mediaFileDownloader.downloadStatusState,
            mediaFileDownloader.videoDownloadProgressState,
            cacheManager.cachingStatusState,
            videoQueueManager.currentVideoMetadataState,
            videoQueueManager.videoQueueLenState,
        ) { downloadStatus, downloadProgress, cachingStatus, meta, queueLen ->
            Tuple5(
                downloadStatus,
                downloadProgress,
                cachingStatus,
                meta,
                queueLen
            )
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