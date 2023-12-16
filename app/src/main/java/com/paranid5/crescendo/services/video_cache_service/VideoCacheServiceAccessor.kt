package com.paranid5.crescendo.services.video_cache_service

import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.MainApplication
import com.paranid5.crescendo.VIDEO_CASH_SERVICE_CONNECTION
import com.paranid5.crescendo.domain.caching.CacheTrimRange
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.services.ServiceAccessor
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class VideoCacheServiceAccessor(application: MainApplication) : ServiceAccessor(application) {
    private val isVideoCashServiceConnectedState by inject<MutableStateFlow<Boolean>>(
        named(VIDEO_CASH_SERVICE_CONNECTION)
    )

    private inline val isVideoCashServiceConnected
        get() = isVideoCashServiceConnectedState.value

    private fun Intent.putVideoCashDataArgs(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: CacheTrimRange
    ) = apply {
        putExtra(VideoCacheService.URL_ARG, videoUrl)
        putExtra(VideoCacheService.FILENAME_ARG, desiredFilename)
        putExtra(VideoCacheService.FORMAT_ARG, format)
        putExtra(VideoCacheService.TRIM_RANGE_ARG, trimRange)
    }

    private fun startVideoCashService(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: CacheTrimRange
    ) {
        val serviceIntent = Intent(appContext, VideoCacheService::class.java)
            .putVideoCashDataArgs(videoUrl, desiredFilename, format, trimRange)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            appContext.startForegroundService(serviceIntent)
        else
            appContext.startService(serviceIntent)
    }

    private fun cashNextVideo(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: CacheTrimRange
    ) = sendBroadcast(
        Intent(VideoCacheService.Broadcast_CASH_NEXT_VIDEO)
            .putVideoCashDataArgs(videoUrl, desiredFilename, format, trimRange)
    )

    private fun startCashing(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: CacheTrimRange
    ) = startVideoCashService(videoUrl, desiredFilename, format, trimRange)

    fun startCashingOrAddToQueue(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: CacheTrimRange
    ) = when {
        isVideoCashServiceConnected -> cashNextVideo(
            videoUrl,
            desiredFilename,
            format,
            trimRange
        )

        else -> startCashing(videoUrl, desiredFilename, format, trimRange)
    }
}