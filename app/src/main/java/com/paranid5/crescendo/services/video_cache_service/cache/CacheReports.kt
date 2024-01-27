package com.paranid5.crescendo.services.video_cache_service.cache

import android.content.Intent
import com.paranid5.crescendo.domain.caching.CachingResult
import com.paranid5.crescendo.domain.caching.VideoCacheResponse
import com.paranid5.crescendo.domain.utils.extensions.sendBroadcastCompat
import com.paranid5.crescendo.receivers.CacheStatusReceiver
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService

fun VideoCacheService.reportCachingResult(cachingResult: CachingResult) =
    when (cachingResult) {
        is CachingResult.DownloadResult.Success -> true

        CachingResult.DownloadResult.FileCreationError -> reportFileCreationError()

        CachingResult.DownloadResult.ConnectionLostError -> reportConnectionLostError()

        is CachingResult.DownloadResult.Error -> reportDownloadError(
            code = cachingResult.statusCode.value,
            description = cachingResult.statusCode.description
        )

        CachingResult.ConversionError -> reportAudioConversionError()

        CachingResult.Canceled -> reportVideoCacheCanceled()

        is CachingResult.Success -> reportVideoCacheSuccessful()
    }

private fun VideoCacheService.reportVideoCacheSuccessful() =
    sendBroadcastCompat(
        Intent(this, CacheStatusReceiver::class.java)
            .setAction(CacheStatusReceiver.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(
                CacheStatusReceiver.VIDEO_CASH_STATUS_ARG,
                VideoCacheResponse.Success
            )
    )

private fun VideoCacheService.reportDownloadError(code: Int, description: String) =
    sendBroadcastCompat(
        Intent(CacheStatusReceiver.Broadcast_VIDEO_CASH_COMPLETED).putExtra(
            CacheStatusReceiver.VIDEO_CASH_STATUS_ARG,
            VideoCacheResponse.Error(code, description)
        )
    )

private fun VideoCacheService.reportVideoCacheCanceled() =
    sendBroadcastCompat(
        Intent(CacheStatusReceiver.Broadcast_VIDEO_CASH_COMPLETED).putExtra(
            CacheStatusReceiver.VIDEO_CASH_STATUS_ARG,
            VideoCacheResponse.Canceled
        )
    )

private fun VideoCacheService.reportAudioConversionError() =
    sendBroadcastCompat(
        Intent(CacheStatusReceiver.Broadcast_VIDEO_CASH_COMPLETED).putExtra(
            CacheStatusReceiver.VIDEO_CASH_STATUS_ARG,
            VideoCacheResponse.AudioConversionError
        )
    )

private fun VideoCacheService.reportFileCreationError() =
    sendBroadcastCompat(
        Intent(CacheStatusReceiver.Broadcast_VIDEO_CASH_COMPLETED).putExtra(
            CacheStatusReceiver.VIDEO_CASH_STATUS_ARG,
            VideoCacheResponse.FileCreationError
        )
    )

private fun VideoCacheService.reportConnectionLostError() =
    sendBroadcastCompat(
        Intent(CacheStatusReceiver.Broadcast_VIDEO_CASH_COMPLETED).putExtra(
            CacheStatusReceiver.VIDEO_CASH_STATUS_ARG,
            VideoCacheResponse.ConnectionLostError
        )
    )