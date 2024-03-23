package com.paranid5.crescendo.system.services.stream

import android.content.Context
import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.core.impl.di.STREAM_SERVICE_CONNECTION
import com.paranid5.crescendo.system.common.broadcast.StreamServiceBroadcasts
import com.paranid5.crescendo.system.common.broadcast.TrackServiceBroadcasts
import com.paranid5.system.services.common.ServiceAccessor
import com.paranid5.system.services.common.ServiceAccessorImpl
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
    sendBroadcast(StreamServiceBroadcasts.Broadcast_10_SECS_BACK)

fun StreamServiceAccessor.sendSeekTo10SecsForwardBroadcast() =
    sendBroadcast(StreamServiceBroadcasts.Broadcast_10_SECS_FORWARD)

fun StreamServiceAccessor.sendSeekToBroadcast(position: Long) = sendBroadcast(
    Intent(StreamServiceBroadcasts.Broadcast_SEEK_TO)
        .putExtra(StreamServiceBroadcasts.POSITION_ARG, position)
)

fun StreamServiceAccessor.sendPauseBroadcast() =
    sendBroadcast(StreamServiceBroadcasts.Broadcast_PAUSE)

fun StreamServiceAccessor.sendChangeRepeatBroadcast() =
    sendBroadcast(StreamServiceBroadcasts.Broadcast_REPEAT_CHANGED)

private fun StreamServiceAccessor.sendResumeBroadcast() =
    sendBroadcast(StreamServiceBroadcasts.Broadcast_RESUME)

private fun StreamServiceAccessor.sendSwitchToNextStreamBroadcast(url: String) =
    sendBroadcast(Intent(StreamServiceBroadcasts.Broadcast_SWITCH_VIDEO).putStreamUrlIfNotNull(url))

private fun StreamServiceAccessor.sendStopTrackServiceBroadcast() =
    sendBroadcast(TrackServiceBroadcasts.Broadcast_STOP)

private fun StreamServiceAccessor.startStreamService(url: String? = null) {
    val serviceIntent = Intent(appContext, StreamService::class.java)
        .putStreamUrlIfNotNull(url)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        appContext.startForegroundService(serviceIntent)
    else
        appContext.startService(serviceIntent)
}

private fun Intent.putStreamUrlIfNotNull(url: String?) = apply {
    if (url != null) putExtra(StreamServiceBroadcasts.URL_ARG, url)
}