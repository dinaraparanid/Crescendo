package com.paranid5.mediastreamer.presentation.ui_handlers

import android.content.Intent
import com.paranid5.mediastreamer.MainApplication
import com.paranid5.mediastreamer.StreamService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StreamingUIHandler : UIHandler, KoinComponent {
    private val application by inject<MainApplication>()

    private fun sendBroadcast(intent: Intent) = application.sendBroadcast(intent)
    private fun sendBroadcast(action: String) = sendBroadcast(Intent(action))

    fun sendSeekTo10SecsBackBroadcast() = sendBroadcast(StreamService.Broadcast_10_SECS_BACK)

    fun sendSeekTo10SecsForwardBroadcast() = sendBroadcast(StreamService.Broadcast_10_SECS_FORWARD)

    fun sendSeekToBroadcast(position: Long) = sendBroadcast(
        Intent(StreamService.Broadcast_SEEK_TO)
            .putExtra(StreamService.POSITION_ARG, position)
    )

    fun sendPauseBroadcast() = sendBroadcast(StreamService.Broadcast_PAUSE)

    fun sendResumeBroadcast() = sendBroadcast(StreamService.Broadcast_RESUME)

    fun sendSwitchVideoBroadcast(newUrl: String) = sendBroadcast(
        Intent(StreamService.Broadcast_SWITCH_VIDEO)
            .putExtra(StreamService.URL_ARG, newUrl)
    )

    fun sendRepeatBroadcast() = sendBroadcast(StreamService.Broadcast_CHANGE_REPEAT)
}