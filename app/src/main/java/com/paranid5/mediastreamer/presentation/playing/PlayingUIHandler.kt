package com.paranid5.mediastreamer.presentation.playing

import android.content.Context
import android.widget.Toast
import com.paranid5.mediastreamer.AUDIO_SESSION_ID
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.services.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.domain.services.track_service.TrackServiceAccessor
import com.paranid5.mediastreamer.domain.services.video_cash_service.Formats
import com.paranid5.mediastreamer.domain.services.video_cash_service.VideoCashServiceAccessor
import com.paranid5.mediastreamer.presentation.NavHostController
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.UIHandler
import com.paranid5.mediastreamer.presentation.ui.handleOrIgnore
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class PlayingUIHandler(
    private val storageHandler: StorageHandler,
    private val streamServiceAccessor: StreamServiceAccessor,
    private val trackServiceAccessor: TrackServiceAccessor,
    private val videoCashServiceAccessor: VideoCashServiceAccessor
) : UIHandler, KoinComponent {
    private val audioSessionIdState by inject<MutableStateFlow<Int>>(named(AUDIO_SESSION_ID))
    private val audioStatusState = storageHandler.audioStatusState

    private inline val audioStatus
        get() = audioStatusState.value

    fun sendOnPrevButtonClickedBroadcast() = audioStatus.handleOrIgnore(
        streamAction = streamServiceAccessor::sendSeekTo10SecsBackBroadcast,
        trackAction = trackServiceAccessor::sendSwitchToPrevTrackBroadcast
    )

    fun sendOnNextButtonClickedBroadcast() = audioStatus.handleOrIgnore(
        streamAction = streamServiceAccessor::sendSeekTo10SecsForwardBroadcast,
        trackAction = trackServiceAccessor::sendSwitchToNextTrackBroadcast
    )

    fun sendSeekToBroadcast(position: Long) = audioStatus.handleOrIgnore(
        streamAction = { streamServiceAccessor.sendSeekToBroadcast(position) },
        trackAction = { trackServiceAccessor.sendSeekToBroadcast(position) }
    )

    fun sendPauseBroadcast() = audioStatus.handleOrIgnore(
        streamAction = streamServiceAccessor::sendPauseBroadcast,
        trackAction = trackServiceAccessor::sendPauseBroadcast
    )

    fun startStreamingOrSendResumeBroadcast() = audioStatus.handleOrIgnore(
        streamAction = streamServiceAccessor::startStreamingOrSendResumeBroadcast,
        trackAction = trackServiceAccessor::startStreamingOrSendResumeBroadcast
    )

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
                Screens.Audio.AudioEffects
            )
        }
    }
}