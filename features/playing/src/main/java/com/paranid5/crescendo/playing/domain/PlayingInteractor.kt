package com.paranid5.crescendo.playing.domain

import android.content.Context
import android.widget.Toast
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.PlaybackRepository.Companion.UNDEFINED_AUDIO_SESSION_ID
import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor
import com.paranid5.crescendo.system.services.stream.sendPauseBroadcast
import com.paranid5.crescendo.system.services.stream.sendSeekTo10SecsBackBroadcast
import com.paranid5.crescendo.system.services.stream.sendSeekTo10SecsForwardBroadcast
import com.paranid5.crescendo.system.services.stream.sendSeekToBroadcast
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor

internal class PlayingInteractor(
    private val streamServiceAccessor: StreamServiceAccessor,
    private val trackServiceInteractor: TrackServiceInteractor,
    private val playbackRepository: PlaybackRepository,
) {
    fun sendOnPrevButtonClickedBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::sendSeekTo10SecsBackBroadcast,
        trackAction = trackServiceInteractor::sendSwitchToPrevTrackBroadcast
    )

    fun sendOnNextButtonClickedBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::sendSeekTo10SecsForwardBroadcast,
        trackAction = trackServiceInteractor::sendSwitchToNextTrackBroadcast
    )

    fun sendSeekToBroadcast(audioStatus: AudioStatus, position: Long) = audioStatus.handle(
        streamAction = { streamServiceAccessor.sendSeekToBroadcast(position) },
        trackAction = { trackServiceInteractor.sendSeekToBroadcast(position) }
    )

    fun sendPauseBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::sendPauseBroadcast,
        trackAction = trackServiceInteractor::sendPauseBroadcast
    )

    fun startStreamingOrSendResumeBroadcast(audioStatus: AudioStatus) = audioStatus.handle(
        streamAction = streamServiceAccessor::startStreamingOrSendResumeBroadcast,
        trackAction = trackServiceInteractor::startStreamingOrSendResumeBroadcast
    )

    fun tryNavigateToAudioEffects(context: Context, navigate: () -> Unit) {
        when (playbackRepository.audioSessionIdState.value) {
            UNDEFINED_AUDIO_SESSION_ID -> Toast.makeText(
                context,
                R.string.audio_effects_init_error,
                Toast.LENGTH_LONG
            ).show()

            else -> navigate()
        }
    }
}
