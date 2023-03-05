package com.paranid5.mediastreamer.presentation.streaming

import com.paranid5.mediastreamer.StorageHandler
import com.paranid5.mediastreamer.presentation.UIHandler
import com.paranid5.mediastreamer.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.video_cash_service.VideoCashServiceAccessor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StreamingUIHandler(private val serviceAccessor: StreamServiceAccessor) :
    UIHandler, KoinComponent {
    private val storageHandler by inject<StorageHandler>()
    private val videoCashServiceAccessor by inject<VideoCashServiceAccessor>()

    fun sendSeekTo10SecsBackBroadcast() = serviceAccessor.sendSeekTo10SecsBackBroadcast()

    fun sendSeekTo10SecsForwardBroadcast() = serviceAccessor.sendSeekTo10SecsForwardBroadcast()

    fun sendSeekToBroadcast(position: Long) = serviceAccessor.sendSeekToBroadcast(position)

    fun sendPauseBroadcast() = serviceAccessor.sendPauseBroadcast()

    fun startStreamingOrSendResumeBroadcast() =
        serviceAccessor.startStreamingOrSendResumeBroadcast()

    fun sendChangeRepeatBroadcast() = serviceAccessor.sendChangeRepeatBroadcast()

    fun launchVideoCashService(desiredFilename: String, isSaveAsVideo: Boolean) =
        videoCashServiceAccessor.startCashingOrAddToQueue(
            videoUrl = storageHandler.currentUrlState.value,
            desiredFilename = desiredFilename,
            isSaveAsVideo = isSaveAsVideo
        )
}