package com.paranid5.crescendo.domain.services.stream_service

import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.MainApplication
import com.paranid5.crescendo.STREAM_SERVICE_CONNECTION
import com.paranid5.crescendo.domain.services.ServiceAccessor
import com.paranid5.crescendo.domain.services.track_service.TrackService
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class StreamServiceAccessor(application: MainApplication) : ServiceAccessor(application) {
    private val isStreamServiceConnectedState by inject<MutableStateFlow<Boolean>>(
        named(STREAM_SERVICE_CONNECTION)
    )

    private inline val isStreamServiceConnected
        get() = isStreamServiceConnectedState.value

    private fun Intent.putStreamUrlIfNotNull(url: String?) = apply {
        if (url != null) putExtra(StreamService.URL_ARG, url)
    }

    private fun startStreamService(url: String? = null) {
        val serviceIntent = Intent(appContext, StreamService::class.java)
            .putStreamUrlIfNotNull(url)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            appContext.startForegroundService(serviceIntent)
        else
            appContext.startService(serviceIntent)
    }

    private fun switchToNextStream(url: String) = sendBroadcast(
        Intent(StreamService.Broadcast_SWITCH_VIDEO).putStreamUrlIfNotNull(url)
    )

    private fun launchStreamService(url: String) = when {
        isStreamServiceConnected -> switchToNextStream(url)
        else -> startStreamService(url)
    }

    private fun stopTrackService() = sendBroadcast(TrackService.Broadcast_STOP)

    fun startStreaming(url: String) {
        stopTrackService()
        launchStreamService(url)
    }

    fun sendSeekTo10SecsBackBroadcast() = sendBroadcast(StreamService.Broadcast_10_SECS_BACK)

    fun sendSeekTo10SecsForwardBroadcast() = sendBroadcast(StreamService.Broadcast_10_SECS_FORWARD)

    fun sendSeekToBroadcast(position: Long) = sendBroadcast(
        Intent(StreamService.Broadcast_SEEK_TO)
            .putExtra(StreamService.POSITION_ARG, position)
    )

    fun sendPauseBroadcast() = sendBroadcast(StreamService.Broadcast_PAUSE)

    private fun sendResumeBroadcast() = sendBroadcast(StreamService.Broadcast_RESUME)

    fun startStreamingOrSendResumeBroadcast() {
        stopTrackService()

        when {
            isStreamServiceConnected -> sendResumeBroadcast()
            else -> startStreamService()
        }
    }

    fun sendChangeRepeatBroadcast() = sendBroadcast(StreamService.Broadcast_CHANGE_REPEAT)
}