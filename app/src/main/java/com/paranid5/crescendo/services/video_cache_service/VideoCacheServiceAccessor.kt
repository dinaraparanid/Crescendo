package com.paranid5.crescendo.services.video_cache_service

import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.MainApplication
import com.paranid5.crescendo.VIDEO_CACHE_SERVICE_CONNECTION
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.services.ServiceAccessor
import com.paranid5.crescendo.services.ServiceAccessorImpl
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class VideoCacheServiceAccessor(application: MainApplication) : KoinComponent,
    ServiceAccessor by ServiceAccessorImpl(application) {
    private val isVideoCacheServiceConnectedState by inject<MutableStateFlow<Boolean>>(
        named(VIDEO_CACHE_SERVICE_CONNECTION)
    )

    private inline val isVideoCacheServiceConnected
        get() = isVideoCacheServiceConnectedState.value

    private fun Intent.putVideoCacheDataArgs(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange
    ) = apply {
        putExtra(VideoCacheService.URL_ARG, videoUrl)
        putExtra(VideoCacheService.FILENAME_ARG, desiredFilename)
        putExtra(VideoCacheService.FORMAT_ARG, format)
        putExtra(VideoCacheService.TRIM_RANGE_ARG, trimRange)
    }

    private fun startVideoCacheService(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange
    ) {
        val serviceIntent = Intent(appContext, VideoCacheService::class.java)
            .putVideoCacheDataArgs(videoUrl, desiredFilename, format, trimRange)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            appContext.startForegroundService(serviceIntent)
        else
            appContext.startService(serviceIntent)
    }

    private fun cacheNextVideo(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange
    ) = sendBroadcast(
        Intent(VideoCacheService.Broadcast_CACHE_NEXT_VIDEO)
            .putVideoCacheDataArgs(videoUrl, desiredFilename, format, trimRange)
    )

    private fun startCaching(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange
    ) = startVideoCacheService(videoUrl, desiredFilename, format, trimRange)

    fun startCachingOrAddToQueue(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange
    ) = when {
        isVideoCacheServiceConnected -> cacheNextVideo(
            videoUrl,
            desiredFilename,
            format,
            trimRange
        )

        else -> startCaching(videoUrl, desiredFilename, format, trimRange)
    }
}