package com.paranid5.mediastreamer.video_cash_service

import android.app.Service
import android.content.Intent
import android.os.Build
import com.paranid5.mediastreamer.MainApplication
import com.paranid5.mediastreamer.ServiceAccessor

class VideoCashServiceAccessor(application: MainApplication) : ServiceAccessor(application) {
    private fun Intent.putVideoCashDataArgs(
        videoUrl: String,
        desiredFilename: String,
        isSaveAsVideo: Boolean
    ) = apply {
        putExtra(VideoCashService.URL_ARG, videoUrl)
        putExtra(VideoCashService.FILENAME_ARG, desiredFilename)
        putExtra(VideoCashService.SAVE_AS_VIDEO_ARG, isSaveAsVideo)
    }

    private fun startVideoCashService(
        videoUrl: String,
        desiredFilename: String,
        isSaveAsVideo: Boolean
    ) {
        val serviceIntent = Intent(appContext, VideoCashService::class.java)
            .putVideoCashDataArgs(videoUrl, desiredFilename, isSaveAsVideo)

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
        isSaveAsVideo: Boolean
    ) = sendBroadcast(
        Intent(VideoCashService.Broadcast_CASH_NEXT_VIDEO)
            .putVideoCashDataArgs(videoUrl, desiredFilename, isSaveAsVideo)
    )

    private fun startCashing(videoUrl: String, desiredFilename: String, isSaveAsVideo: Boolean) =
        startVideoCashService(videoUrl, desiredFilename, isSaveAsVideo)

    fun startCashingOrAddToQueue(
        videoUrl: String,
        desiredFilename: String,
        isSaveAsVideo: Boolean
    ) = when {
        application.isVideoCashServiceConnected -> cashNextVideo(
            videoUrl,
            desiredFilename,
            isSaveAsVideo
        )

        else -> startCashing(videoUrl, desiredFilename, isSaveAsVideo)
    }
}