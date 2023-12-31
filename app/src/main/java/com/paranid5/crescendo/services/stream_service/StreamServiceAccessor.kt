package com.paranid5.crescendo.services.stream_service

import android.content.Context
import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.STREAM_SERVICE_CONNECTION
import com.paranid5.crescendo.services.ServiceAccessor
import com.paranid5.crescendo.services.ServiceAccessorImpl
import com.paranid5.crescendo.services.track_service.TrackService
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class StreamServiceAccessor(context: Context) :
    ServiceAccessor by ServiceAccessorImpl(context),
    KoinComponent {
    private val isStreamServiceConnectedState by inject<MutableStateFlow<Boolean>>(
        named(STREAM_SERVICE_CONNECTION)
    )

    private inline val isStreamServiceConnected
        get() = isStreamServiceConnectedState.value

    fun startStreaming(url: String) {
        sendStopTrackServiceBroadcast()
        launchStreamService(url)
    }

    fun startStreamingOrSendResumeBroadcast() {
        sendStopTrackServiceBroadcast()

        when {
            isStreamServiceConnected -> sendResumeBroadcast()
            else -> startStreamService()
        }
    }

    private fun launchStreamService(url: String) = when {
        isStreamServiceConnected -> sendSwitchToNextStreamBroadcast(url)
        else -> startStreamService(url)
    }
}

fun StreamServiceAccessor.sendSeekTo10SecsBackBroadcast() =
    sendBroadcast(StreamService.Broadcast_10_SECS_BACK)

fun StreamServiceAccessor.sendSeekTo10SecsForwardBroadcast() =
    sendBroadcast(StreamService.Broadcast_10_SECS_FORWARD)

fun StreamServiceAccessor.sendSeekToBroadcast(position: Long) = sendBroadcast(
    Intent(StreamService.Broadcast_SEEK_TO)
        .putExtra(StreamService.POSITION_ARG, position)
)

fun StreamServiceAccessor.sendPauseBroadcast() =
    sendBroadcast(StreamService.Broadcast_PAUSE)

fun StreamServiceAccessor.sendChangeRepeatBroadcast() =
    sendBroadcast(StreamService.Broadcast_CHANGE_REPEAT)

private fun StreamServiceAccessor.sendResumeBroadcast() =
    sendBroadcast(StreamService.Broadcast_RESUME)

private fun StreamServiceAccessor.sendSwitchToNextStreamBroadcast(url: String) =
    sendBroadcast(Intent(StreamService.Broadcast_SWITCH_VIDEO).putStreamUrlIfNotNull(url))

private fun StreamServiceAccessor.sendStopTrackServiceBroadcast() =
    sendBroadcast(TrackService.Broadcast_STOP)

private fun StreamServiceAccessor.startStreamService(url: String? = null) {
    val serviceIntent = Intent(appContext, StreamService::class.java)
        .putStreamUrlIfNotNull(url)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        appContext.startForegroundService(serviceIntent)
    else
        appContext.startService(serviceIntent)
}

private fun Intent.putStreamUrlIfNotNull(url: String?) = apply {
    if (url != null) putExtra(StreamService.URL_ARG, url)
}