package com.paranid5.crescendo.system.services.video_cache.notification

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.core.common.caching.CachingStatus
import com.paranid5.crescendo.core.common.caching.DownloadingStatus
import com.paranid5.crescendo.data.ktor.DownloadingProgress
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.runningFold

private const val DELAY_BETWEEN_EVENTS = 1000L

private data class NotificationData(
    val downloadStatus: DownloadingStatus,
    val downloadProgress: DownloadingProgress,
    val cachingStatus: CachingStatus,
    val meta: VideoMetadata,
    val queueLen: Int,
    val timestamp: Long = System.currentTimeMillis(),
)

private data class NotificationHistory(
    val previous: NotificationData? = null,
    val current: NotificationData? = null,
)

internal suspend fun VideoCacheService.startNotificationMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        combine(
            mediaFileDownloader.downloadStatusState,
            mediaFileDownloader.videoDownloadProgressState,
            cacheManager.cachingStatusState,
            videoQueueManager.currentVideoMetadataState,
            videoQueueManager.videoQueueLenState,
        ) { downloadStatus, downloadProgress, cachingStatus, meta, queueLen ->
            NotificationData(
                downloadStatus = downloadStatus,
                downloadProgress = downloadProgress,
                cachingStatus = cachingStatus,
                meta = meta,
                queueLen = queueLen,
            )
        }.runningFold(initial = NotificationHistory()) { acc, data ->
            NotificationHistory(previous = acc.current, current = data)
        }.mapNotNull { (prev, cur) ->
            val prevTime = prev?.timestamp ?: 0
            val currentTime = cur?.timestamp ?: 0

            if (currentTime - prevTime < DELAY_BETWEEN_EVENTS)
                delay(DELAY_BETWEEN_EVENTS)

            cur
        }.collectLatest { (downloadSt, progress, cacheSt, meta, qLen, _) ->
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