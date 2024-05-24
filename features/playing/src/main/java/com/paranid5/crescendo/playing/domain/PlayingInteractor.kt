package com.paranid5.crescendo.playing.domain

import android.content.Context
import android.widget.Toast
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.impl.di.AUDIO_SESSION_ID
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.navigation.NavHostController
import com.paranid5.crescendo.navigation.Screens
import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor
import com.paranid5.crescendo.system.services.stream.sendPauseBroadcast
import com.paranid5.crescendo.system.services.stream.sendSeekTo10SecsBackBroadcast
import com.paranid5.crescendo.system.services.stream.sendSeekTo10SecsForwardBroadcast
import com.paranid5.crescendo.system.services.stream.sendSeekToBroadcast
import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class PlayingInteractor(
    private val streamServiceAccessor: StreamServiceAccessor,
    private val trackServiceAccessor: TrackServiceAccessor,
) : KoinComponent {
    private val audioSessionIdState by inject<MutableStateFlow<Int>>(named(AUDIO_SESSION_ID))

    internal fun sendOnPrevButtonClickedBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::sendSeekTo10SecsBackBroadcast,
        trackAction = trackServiceAccessor::sendSwitchToPrevTrackBroadcast
    )

    internal fun sendOnNextButtonClickedBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::sendSeekTo10SecsForwardBroadcast,
        trackAction = trackServiceAccessor::sendSwitchToNextTrackBroadcast
    )

    internal fun sendSeekToBroadcast(audioStatus: AudioStatus, position: Long) = audioStatus.handle(
        streamAction = { streamServiceAccessor.sendSeekToBroadcast(position) },
        trackAction = { trackServiceAccessor.sendSeekToBroadcast(position) }
    )

    internal fun sendPauseBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::sendPauseBroadcast,
        trackAction = trackServiceAccessor::sendPauseBroadcast
    )

    internal fun startStreamingOrSendResumeBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::startStreamingOrSendResumeBroadcast,
        trackAction = trackServiceAccessor::startStreamingOrSendResumeBroadcast
    )

    internal fun navigateToAudioEffects(context: Context, navHostController: NavHostController) {
        when (audioSessionIdState.value) {
            0 -> Toast.makeText(
                context,
                R.string.audio_effects_init_error,
                Toast.LENGTH_LONG
            ).show()

            else -> navHostController.navigateIfNotSame(Screens.Audio.AudioEffects)
        }
    }
}