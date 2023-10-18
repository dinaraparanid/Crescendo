package com.paranid5.crescendo.domain.services.video_cash_service

import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.MainApplication
import com.paranid5.crescendo.VIDEO_CASH_SERVICE_CONNECTION
import com.paranid5.crescendo.domain.services.ServiceAccessor
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class VideoCashServiceAccessor(application: MainApplication) : ServiceAccessor(application) {
    private val isVideoCashServiceConnectedState by inject<MutableStateFlow<Boolean>>(
        named(VIDEO_CASH_SERVICE_CONNECTION)
    )

    private inline val isVideoCashServiceConnected
        get() = isVideoCashServiceConnectedState.value

    private fun Intent.putVideoCashDataArgs(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: CashTrimRange
    ) = apply {
        putExtra(VideoCashService.URL_ARG, videoUrl)
        putExtra(VideoCashService.FILENAME_ARG, desiredFilename)
        putExtra(VideoCashService.FORMAT_ARG, format)
        putExtra(VideoCashService.TRIM_RANGE_ARG, trimRange)
    }

    private fun startVideoCashService(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: CashTrimRange
    ) {
        val serviceIntent = Intent(appContext, VideoCashService::class.java)
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
        trimRange: CashTrimRange
    ) = sendBroadcast(
        Intent(VideoCashService.Broadcast_CASH_NEXT_VIDEO)
            .putVideoCashDataArgs(videoUrl, desiredFilename, format, trimRange)
    )

    private fun startCashing(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: CashTrimRange
    ) = startVideoCashService(videoUrl, desiredFilename, format, trimRange)

    fun startCashingOrAddToQueue(
        videoUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: CashTrimRange
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