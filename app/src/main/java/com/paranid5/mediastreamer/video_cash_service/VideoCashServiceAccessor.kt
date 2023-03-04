package com.paranid5.mediastreamer.video_cash_service

import android.app.Service
import android.content.Intent
import android.os.Build
import com.paranid5.mediastreamer.MainApplication
import com.paranid5.mediastreamer.ServiceAccessor

class VideoCashServiceAccessor(application: MainApplication) : ServiceAccessor(application) {
    private fun Intent.putUrlAndSaveAsVideo(videoUrl: String, isSaveAsVideo: Boolean) = apply {
        putExtra(VideoCashService.URL_ARG, videoUrl)
        putExtra(VideoCashService.SAVE_AS_VIDEO_ARG, isSaveAsVideo)
    }

    private fun startVideoCashService(videoUrl: String, isSaveAsVideo: Boolean) {
        val serviceIntent = Intent(appContext, VideoCashService::class.java)
            .putUrlAndSaveAsVideo(videoUrl, isSaveAsVideo)

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

    fun cashNextVideo(videoUrl: String, isSaveAsVideo: Boolean) = sendBroadcast(
        Intent(VideoCashService.Broadcast_CASH_NEXT_VIDEO)
            .putUrlAndSaveAsVideo(videoUrl, isSaveAsVideo)
    )

    fun cancelCurVideo() = sendBroadcast(VideoCashService.Broadcast_CANCEL_CUR_VIDEO)
    fun cancelAll() = sendBroadcast(VideoCashService.Broadcast_CANCEL_ALL)

    private fun startCashing(videoUrl: String, isSaveAsVideo: Boolean) =
        startVideoCashService(videoUrl, isSaveAsVideo)

    fun startCashingOrAddToQueue(videoUrl: String, isSaveAsVideo: Boolean) = when {
        application.isVideoCashServiceConnected -> cashNextVideo(videoUrl, isSaveAsVideo)
        else -> startCashing(videoUrl, isSaveAsVideo)
    }
}