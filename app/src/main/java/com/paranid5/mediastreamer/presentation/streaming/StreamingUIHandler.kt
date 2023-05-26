package com.paranid5.mediastreamer.presentation.streaming

import android.content.Context
import android.widget.Toast
import com.paranid5.mediastreamer.AUDIO_SESSION_ID
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.UIHandler
import com.paranid5.mediastreamer.domain.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.domain.video_cash_service.Formats
import com.paranid5.mediastreamer.domain.video_cash_service.VideoCashServiceAccessor
import com.paranid5.mediastreamer.presentation.NavHostController
import com.paranid5.mediastreamer.presentation.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class StreamingUIHandler(private val serviceAccessor: StreamServiceAccessor) :
    UIHandler, KoinComponent {
    private val storageHandler by inject<StorageHandler>()
    private val videoCashServiceAccessor by inject<VideoCashServiceAccessor>()
    private val audioSessionIdState by inject<MutableStateFlow<Int>>(named(AUDIO_SESSION_ID))

    fun sendSeekTo10SecsBackBroadcast() = serviceAccessor.sendSeekTo10SecsBackBroadcast()

    fun sendSeekTo10SecsForwardBroadcast() = serviceAccessor.sendSeekTo10SecsForwardBroadcast()

    fun sendSeekToBroadcast(position: Long) = serviceAccessor.sendSeekToBroadcast(position)

    fun sendPauseBroadcast() = serviceAccessor.sendPauseBroadcast()

    fun startStreamingOrSendResumeBroadcast() =
        serviceAccessor.startStreamingOrSendResumeBroadcast()

    fun launchVideoCashService(desiredFilename: String, format: Formats) =
        videoCashServiceAccessor.startCashingOrAddToQueue(
            videoUrl = storageHandler.currentUrlState.value,
            desiredFilename = desiredFilename,
            format = format
        )

    fun navigateToAudioEffects(context: Context, navHostController: NavHostController) {
        when (audioSessionIdState.value) {
            0 -> Toast.makeText(
                context,
                R.string.audio_effects_init_error,
                Toast.LENGTH_LONG
            ).show()

            else -> navHostController.navigateIfNotSame(
                Screens.MainScreens.StreamScreens.AudioEffects
            )
        }
    }
}