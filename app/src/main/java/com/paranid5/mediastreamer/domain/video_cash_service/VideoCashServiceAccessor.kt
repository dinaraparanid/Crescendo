package com.paranid5.mediastreamer.domain.video_cash_service

import android.app.Service
import android.content.Intent
import android.os.Build
import com.paranid5.mediastreamer.MainApplication
import com.paranid5.mediastreamer.domain.ServiceAccessor

class VideoCashServiceAccessor(application: MainApplication) : ServiceAccessor(application) {
    private fun Intent.putVideoCashDataArgs(
        videoUrl: String,
        desiredFilename: String,
        format: Formats
    ) = apply {
        putExtra(VideoCashService.URL_ARG, videoUrl)
        putExtra(VideoCashService.FILENAME_ARG, desiredFilename)
        putExtra(VideoCashService.FORMAT_ARG, format)
    }

    private fun startVideoCashService(
        videoUrl: String,
        desiredFilename: String,
        format: Formats
    ) {
        val serviceIntent = Intent(appContext, VideoCashService::class.java)
            .putVideoCashDataArgs(videoUrl, desiredFilename, format)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            appContext.startForegroundService(serviceIntent)
        else
            appContext.startService(serviceIntent)

        appContext.bindService(
            serviceIntent,
            application.videoCashServiceConnection,
            Service.BIND_AUTO_CREATE
        )
    }

    private fun cashNextVideo(
        videoUrl: String,
        desiredFilename: String,
        format: Formats
    ) = sendBroadcast(
        Intent(VideoCashService.Broadcast_CASH_NEXT_VIDEO)
            .putVideoCashDataArgs(videoUrl, desiredFilename, format)
    )

    private fun startCashing(videoUrl: String, desiredFilename: String, format: Formats) =
        startVideoCashService(videoUrl, desiredFilename, format)

    fun startCashingOrAddToQueue(
        videoUrl: String,
        desiredFilename: String,
        format: Formats
    ) = when {
        application.isVideoCashServiceConnected -> cashNextVideo(
            videoUrl,
            desiredFilename,
            format
        )

        else -> startCashing(videoUrl, desiredFilename, format)
    }
}