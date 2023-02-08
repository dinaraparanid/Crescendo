package com.paranid5.mediastreamer.presentation.streaming

import com.paranid5.mediastreamer.*
import com.paranid5.mediastreamer.presentation.UIHandler
import com.paranid5.mediastreamer.stream_service.StreamServiceAccessor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class StreamingUIHandler(private val serviceAccessor: StreamServiceAccessor) :
    UIHandler, KoinComponent {
    private val storageHandler by inject<StorageHandler>()
    private val titlePlaceholder by inject<String>(named(STREAM_WITH_NO_NAME))

    fun sendSeekTo10SecsBackBroadcast() = serviceAccessor.sendSeekTo10SecsBackBroadcast()

    fun sendSeekTo10SecsForwardBroadcast() = serviceAccessor.sendSeekTo10SecsForwardBroadcast()

    fun sendSeekToBroadcast(position: Long) = serviceAccessor.sendSeekToBroadcast(position)

    fun sendPauseBroadcast() = serviceAccessor.sendPauseBroadcast()

    fun startStreamingOrSendResumeBroadcast() =
        serviceAccessor.startStreamingOrSendResumeBroadcast()

    fun sendChangeRepeatBroadcast() = serviceAccessor.sendChangeRepeatBroadcast()

    fun launchVideoCashWorker(isSaveAsVideo: Boolean) = VideoCashWorker.launch(
        url = storageHandler.currentUrlState.value,
        videoTitle = storageHandler.currentMetadataState.value?.title ?: titlePlaceholder,
        saveAsVideo = isSaveAsVideo
    )
}