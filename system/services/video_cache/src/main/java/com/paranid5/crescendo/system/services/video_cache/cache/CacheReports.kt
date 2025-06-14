package com.paranid5.crescendo.system.services.video_cache.cache

import android.content.Intent
import com.paranid5.crescendo.caching.entity.CachingResult
import com.paranid5.crescendo.caching.entity.VideoCacheResponse
import com.paranid5.crescendo.system.receivers.CacheStatusReceiver
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService
import com.paranid5.crescendo.utils.extensions.sendAppBroadcast

internal fun VideoCacheService.reportCachingResult(cachingResult: CachingResult) =
    when (cachingResult) {
        is CachingResult.DownloadResult.Success -> Unit

        CachingResult.DownloadResult.FileCreationError -> reportFileCreationError()

        CachingResult.DownloadResult.ConnectionLostError -> reportConnectionLostError()

        is CachingResult.DownloadResult.Error ->
            throw IllegalStateException("Downloading was finished with an error, which is not acceptable")

        CachingResult.ConversionError -> reportAudioConversionError()

        CachingResult.Canceled -> reportVideoCacheCanceled()

        is CachingResult.Success -> reportVideoCacheSuccessful()
    }

private fun VideoCacheService.reportVideoCacheSuccessful() = sendAppBroadcast(
    Intent(this, CacheStatusReceiver::class.java)
        .setAction(CacheStatusReceiver.Broadcast_VIDEO_CACHE_COMPLETED)
        .putExtra(
            CacheStatusReceiver.VIDEO_CACHE_STATUS_ARG,
            VideoCacheResponse.Success
        )
)

private fun VideoCacheService.reportVideoCacheCanceled() = sendAppBroadcast(
    Intent(CacheStatusReceiver.Broadcast_VIDEO_CACHE_COMPLETED).putExtra(
        CacheStatusReceiver.VIDEO_CACHE_STATUS_ARG,
        VideoCacheResponse.Canceled
    )
)

private fun VideoCacheService.reportAudioConversionError() = sendAppBroadcast(
    Intent(CacheStatusReceiver.Broadcast_VIDEO_CACHE_COMPLETED).putExtra(
        CacheStatusReceiver.VIDEO_CACHE_STATUS_ARG,
        VideoCacheResponse.AudioConversionError
    )
)

private fun VideoCacheService.reportFileCreationError() = sendAppBroadcast(
    Intent(CacheStatusReceiver.Broadcast_VIDEO_CACHE_COMPLETED).putExtra(
        CacheStatusReceiver.VIDEO_CACHE_STATUS_ARG,
        VideoCacheResponse.FileCreationError
    )
)

private fun VideoCacheService.reportConnectionLostError() = sendAppBroadcast(
    Intent(CacheStatusReceiver.Broadcast_VIDEO_CACHE_COMPLETED).putExtra(
        CacheStatusReceiver.VIDEO_CACHE_STATUS_ARG,
        VideoCacheResponse.ConnectionLostError
    )
)
