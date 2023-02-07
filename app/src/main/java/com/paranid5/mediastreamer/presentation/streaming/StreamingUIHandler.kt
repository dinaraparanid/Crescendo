package com.paranid5.mediastreamer.presentation.streaming

import com.paranid5.mediastreamer.presentation.UIHandler
import com.paranid5.mediastreamer.stream_service.StreamServiceAccessor

class StreamingUIHandler(private val serviceAccessor: StreamServiceAccessor) : UIHandler {
    fun sendSeekTo10SecsBackBroadcast() = serviceAccessor.sendSeekTo10SecsBackBroadcast()

    fun sendSeekTo10SecsForwardBroadcast() = serviceAccessor.sendSeekTo10SecsForwardBroadcast()

    fun sendSeekToBroadcast(position: Long) = serviceAccessor.sendSeekToBroadcast(position)

    fun sendPauseBroadcast() = serviceAccessor.sendPauseBroadcast()

    fun startStreamingOrSendResumeBroadcast() =
        serviceAccessor.startStreamingOrSendResumeBroadcast()

    fun sendChangeRepeatBroadcast() = serviceAccessor.sendChangeRepeatBroadcast()

    fun sendCashBroadcast(isSaveAsVideo: Boolean) = serviceAccessor.sendCashBroadcast(isSaveAsVideo)
}