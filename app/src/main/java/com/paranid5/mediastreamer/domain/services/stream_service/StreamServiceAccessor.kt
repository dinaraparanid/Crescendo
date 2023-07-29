package com.paranid5.mediastreamer.domain.services.stream_service

import android.app.Service
import android.content.Intent
import android.os.Build
import com.paranid5.mediastreamer.MainApplication
import com.paranid5.mediastreamer.domain.ServiceAccessor

class StreamServiceAccessor(application: MainApplication) : ServiceAccessor(application) {
    private fun Intent.putStreamUrlIfNotNull(url: String?) = apply {
        if (url != null) putExtra(StreamService.URL_ARG, url)
    }

    private fun startStreamService(url: String?) {
        val serviceIntent = Intent(appContext, StreamService::class.java)
            .putStreamUrlIfNotNull(url)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            appContext.startForegroundService(serviceIntent)
        else
            appContext.startService(serviceIntent)

        appContext.bindService(
            serviceIntent,
            application.streamServiceConnection,
            Service.BIND_AUTO_CREATE
        )
    }

    private fun switchToNextStream(url: String?) = sendBroadcast(
        Intent(StreamService.Broadcast_SWITCH_VIDEO).putStreamUrlIfNotNull(url)
    )

    private fun launchStreamService(url: String?) = when {
        application.isStreamServiceConnected -> switchToNextStream(url)
        else -> startStreamService(url)
    }

    fun startStreaming(url: String?) = launchStreamService(url)

    fun sendSeekTo10SecsBackBroadcast() = sendBroadcast(StreamService.Broadcast_10_SECS_BACK)

    fun sendSeekTo10SecsForwardBroadcast() = sendBroadcast(StreamService.Broadcast_10_SECS_FORWARD)

    fun sendSeekToBroadcast(position: Long) = sendBroadcast(
        Intent(StreamService.Broadcast_SEEK_TO)
            .putExtra(StreamService.POSITION_ARG, position)
    )

    fun sendPauseBroadcast() = sendBroadcast(StreamService.Broadcast_PAUSE)

    private fun sendResumeBroadcast() = sendBroadcast(StreamService.Broadcast_RESUME)

    fun startStreamingOrSendResumeBroadcast() = when {
        application.isStreamServiceConnected -> sendResumeBroadcast()
        else -> startStreamService(url = null)
    }

    fun sendChangeRepeatBroadcast() = sendBroadcast(StreamService.Broadcast_CHANGE_REPEAT)
}