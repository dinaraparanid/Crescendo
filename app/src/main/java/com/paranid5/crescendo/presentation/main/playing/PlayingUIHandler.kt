package com.paranid5.crescendo.presentation.main.playing

import android.content.Context
import android.widget.Toast
import com.paranid5.crescendo.AUDIO_SESSION_ID
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.presentation.UIHandler
import com.paranid5.crescendo.presentation.main.NavHostController
import com.paranid5.crescendo.presentation.main.Screens
import com.paranid5.crescendo.services.stream_service.StreamServiceAccessor
import com.paranid5.crescendo.services.stream_service.sendPauseBroadcast
import com.paranid5.crescendo.services.stream_service.sendSeekTo10SecsBackBroadcast
import com.paranid5.crescendo.services.stream_service.sendSeekTo10SecsForwardBroadcast
import com.paranid5.crescendo.services.stream_service.sendSeekToBroadcast
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import com.paranid5.crescendo.services.video_cache_service.VideoCacheServiceAccessor
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

    fun sendOnPrevButtonClickedBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::sendSeekTo10SecsBackBroadcast,
        trackAction = trackServiceAccessor::sendSwitchToPrevTrackBroadcast
    )

    fun sendOnNextButtonClickedBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::sendSeekTo10SecsForwardBroadcast,
        trackAction = trackServiceAccessor::sendSwitchToNextTrackBroadcast
    )

    fun sendSeekToBroadcast(audioStatus: AudioStatus, position: Long) = audioStatus.handle(
        streamAction = { streamServiceAccessor.sendSeekToBroadcast(position) },
        trackAction = { trackServiceAccessor.sendSeekToBroadcast(position) }
    )

    fun sendPauseBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::sendPauseBroadcast,
        trackAction = trackServiceAccessor::sendPauseBroadcast
    )

    fun startStreamingOrSendResumeBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::startStreamingOrSendResumeBroadcast,
        trackAction = trackServiceAccessor::startStreamingOrSendResumeBroadcast
    )

    fun launchVideoCacheService(
        url: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange
    ) = videoCacheServiceAccessor.startCachingOrAddToQueue(
        videoUrl = url,
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