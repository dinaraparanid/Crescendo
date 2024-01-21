package com.paranid5.crescendo.services.video_cache_service.notification

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Tuple6
import com.paranid5.crescendo.domain.caching.CachingStatus
import com.paranid5.crescendo.domain.caching.DownloadError
import com.paranid5.crescendo.domain.caching.DownloadingStatus
import com.paranid5.crescendo.domain.ktor_client.DownloadingProgress
import com.paranid5.crescendo.domain.metadata.VideoMetadata
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

suspend inline fun VideoCacheService.startNotificationMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        combine(
            mediaFileDownloader.downloadStatusState,
            mediaFileDownloader.videoDownloadProgressState,
            mediaFileDownloader.downloadErrorState,
            cacheManager.cachingStatusState,
            videoQueueManager.currentVideoMetadataState,
            videoQueueManager.videoQueueLenState,
        ) { args ->
            Tuple6(
                args[0] as DownloadingStatus,
                args[1] as DownloadingProgress,
                args[2] as DownloadError,
                args[3] as CachingStatus,
                args[4] as VideoMetadata,
                args[5] as Int
            )
        }.collectLatest { (downloadSt, progress, error, cacheSt, meta, qLen) ->
            notificationManager.showNotification(
                service = this@startNotificationMonitoring,
                downloadStatus = downloadSt,
                cacheStatus = cacheSt,
                videoMetadata = meta,
                videoQueueLen = qLen,
                downloadedBytes = progress.downloadedBytes,
                totalBytes = progress.totalBytes,
                errorCode = error.errorCode,
                errorDescription = error.errorDescription
            )
        }
    }