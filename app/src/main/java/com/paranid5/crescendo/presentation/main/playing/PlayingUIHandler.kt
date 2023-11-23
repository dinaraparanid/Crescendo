package com.paranid5.crescendo.presentation.main.playing

import android.content.Context
import android.widget.Toast
import com.paranid5.crescendo.AUDIO_SESSION_ID
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.domain.services.stream_service.StreamServiceAccessor
import com.paranid5.crescendo.domain.services.track_service.TrackServiceAccessor
import com.paranid5.crescendo.domain.services.video_cache_service.CacheTrimRange
import com.paranid5.crescendo.domain.services.video_cache_service.Formats
import com.paranid5.crescendo.domain.services.video_cache_service.VideoCacheServiceAccessor
import com.paranid5.crescendo.presentation.main.NavHostController
import com.paranid5.crescendo.presentation.main.Screens
import com.paranid5.crescendo.presentation.UIHandler
import com.paranid5.crescendo.presentation.main.AudioStatus
import com.paranid5.crescendo.presentation.main.handleOrIgnore
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class PlayingUIHandler(
    private val storageHandler: StorageHandler,
    private val streamServiceAccessor: StreamServiceAccessor,
    private val trackServiceAccessor: TrackServiceAccessor,
    private val videoCacheServiceAccessor: VideoCacheServiceAccessor
) : UIHandler, KoinComponent {
    private val audioSessionIdState by inject<MutableStateFlow<Int>>(named(AUDIO_SESSION_ID))

    fun sendOnPrevButtonClickedBroadcast(audioStatus: AudioStatus?) = audioStatus.handleOrIgnore(
        streamAction = streamServiceAccessor::sendSeekTo10SecsBackBroadcast,
        trackAction = trackServiceAccessor::sendSwitchToPrevTrackBroadcast
    )

    fun sendOnNextButtonClickedBroadcast(audioStatus: AudioStatus?) = audioStatus.handleOrIgnore(
        streamAction = streamServiceAccessor::sendSeekTo10SecsForwardBroadcast,
        trackAction = trackServiceAccessor::sendSwitchToNextTrackBroadcast
    )

    fun sendSeekToBroadcast(audioStatus: AudioStatus?, position: Long) = audioStatus.handleOrIgnore(
        streamAction = { streamServiceAccessor.sendSeekToBroadcast(position) },
        trackAction = { trackServiceAccessor.sendSeekToBroadcast(position) }
    )

    fun sendPauseBroadcast(audioStatus: AudioStatus?) = audioStatus.handleOrIgnore(
        streamAction = streamServiceAccessor::sendPauseBroadcast,
        trackAction = trackServiceAccessor::sendPauseBroadcast
    )

    fun startStreamingOrSendResumeBroadcast(audioStatus: AudioStatus?) = audioStatus.handleOrIgnore(
        streamAction = streamServiceAccessor::startStreamingOrSendResumeBroadcast,
        trackAction = trackServiceAccessor::startStreamingOrSendResumeBroadcast
    )

    fun launchVideoCashService(
        desiredFilename: String,
        format: Formats,
        trimRange: CacheTrimRange
    ) = videoCacheServiceAccessor.startCashingOrAddToQueue(
        videoUrl = storageHandler.currentUrlState.value,
        desiredFilename = desiredFilename,
        format = format,
        trimRange = trimRange
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