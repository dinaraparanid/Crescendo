package com.paranid5.mediastreamer.presentation.playing

import android.content.Context
import android.widget.Toast
import com.paranid5.mediastreamer.AUDIO_SESSION_ID
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.domain.track_service.TrackServiceAccessor
import com.paranid5.mediastreamer.domain.video_cash_service.Formats
import com.paranid5.mediastreamer.domain.video_cash_service.VideoCashServiceAccessor
import com.paranid5.mediastreamer.presentation.NavHostController
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.UIHandler
import com.paranid5.mediastreamer.presentation.ui.AudioStatus
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

    fun sendSeekTo10SecsBackBroadcast() = when (audioStatus) {
        AudioStatus.STREAMING -> streamServiceAccessor.sendSeekTo10SecsBackBroadcast()
        AudioStatus.PLAYING -> trackServiceAccessor.sendSwitchToPrevTrackBroadcast()
        else -> throw NullPointerException("Audio system was not initialized")
    }

    fun sendSeekTo10SecsForwardBroadcast() = when (audioStatus) {
        AudioStatus.STREAMING -> streamServiceAccessor.sendSeekTo10SecsForwardBroadcast()
        AudioStatus.PLAYING -> trackServiceAccessor.sendSwitchToNextTrackBroadcast()
        else -> throw NullPointerException("Audio system was not initialized")
    }

    fun sendSeekToBroadcast(position: Long) = when (audioStatus) {
        AudioStatus.STREAMING -> streamServiceAccessor.sendSeekToBroadcast(position)
        AudioStatus.PLAYING -> trackServiceAccessor.sendSeekToBroadcast(position)
        else -> throw NullPointerException("Audio system was not initialized")
    }

    fun sendPauseBroadcast() = when (audioStatus) {
        AudioStatus.STREAMING -> streamServiceAccessor.sendPauseBroadcast()
        AudioStatus.PLAYING -> trackServiceAccessor.sendPauseBroadcast()
        else -> throw NullPointerException("Audio system was not initialized")
    }

    fun startStreamingOrSendResumeBroadcast() = when (audioStatus) {
        AudioStatus.STREAMING -> streamServiceAccessor.startStreamingOrSendResumeBroadcast()
        AudioStatus.PLAYING -> trackServiceAccessor.startStreamingOrSendResumeBroadcast()
        else -> throw NullPointerException("Audio system was not initialized")
    }

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