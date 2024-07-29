package com.paranid5.crescendo.cache.domain

import com.paranid5.crescendo.cache.presentation.CacheViewModel
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.system.services.video_cache.VideoCacheServiceAccessor

internal class CacheInteractor(private val videoCacheServiceAccessor: VideoCacheServiceAccessor) {
    suspend fun startCaching(
        url: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange,
        viewModel: CacheViewModel
    ) {
        viewModel.updateDownloadingUrl(url)

        launchVideoCacheService(
            url = url,
            desiredFilename = desiredFilename,
            format = format,
            trimRange = trimRange
        )
    }

    private fun launchVideoCacheService(
        url: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange
    ) = videoCacheServiceAccessor.startCachingOrAddToQueue(
        videoUrl = url,
        desiredFilename = desiredFilename,
        format = format,
        trimRange = trimRange
    )
}