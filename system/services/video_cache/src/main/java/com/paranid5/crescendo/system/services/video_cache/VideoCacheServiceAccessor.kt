package com.paranid5.crescendo.system.services.video_cache

import android.content.Context
import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.core.impl.di.VIDEO_CACHE_SERVICE_CONNECTION
import com.paranid5.crescendo.core.impl.trimmer.TrimRangeModel
import com.paranid5.crescendo.system.common.broadcast.VideoCacheServiceBroadcasts
import com.paranid5.system.services.common.ServiceAccessor
import com.paranid5.system.services.common.ServiceAccessorImpl
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class VideoCacheServiceAccessor(context: Context) : KoinComponent,
    ServiceAccessor by ServiceAccessorImpl(context) {
    private val isVideoCacheServiceConnectedState by inject<MutableStateFlow<Boolean>>(
        named(VIDEO_CACHE_SERVICE_CONNECTION)
    )

    private inline val isVideoCacheServiceConnected
        get() = isVideoCacheServiceConnectedState.value

    private fun Intent.putVideoCacheDataArgs(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange,
    ) = apply {
        putExtra(VideoCacheServiceBroadcasts.URL_ARG, videoUrl)
        putExtra(VideoCacheServiceBroadcasts.FILENAME_ARG, desiredFilename)
        putExtra(VideoCacheServiceBroadcasts.FORMAT_ARG, format)
        putExtra(VideoCacheServiceBroadcasts.TRIM_RANGE_ARG, TrimRangeModel(trimRange))
    }

    private fun startVideoCacheService(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange,
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
        trimRange: TrimRange,
    ) = sendBroadcast(
        Intent(VideoCacheServiceBroadcasts.Broadcast_CACHE_NEXT_VIDEO).putVideoCacheDataArgs(
            videoUrl = videoUrl,
            desiredFilename = desiredFilename,
            format = format,
            trimRange = trimRange,
        )
    )

    private fun startCaching(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange,
    ) = startVideoCacheService(
        videoUrl = videoUrl,
        desiredFilename = desiredFilename,
        format = format,
        trimRange = trimRange,
    )

    fun startCachingOrAddToQueue(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange,
    ) = when {
        isVideoCacheServiceConnected -> cacheNextVideo(
            videoUrl = videoUrl,
            desiredFilename = desiredFilename,
            format = format,
            trimRange = trimRange,
        )

        else -> startCaching(
            videoUrl = videoUrl,
            desiredFilename = desiredFilename,
            format = format,
            trimRange = trimRange,
        )
    }
}
