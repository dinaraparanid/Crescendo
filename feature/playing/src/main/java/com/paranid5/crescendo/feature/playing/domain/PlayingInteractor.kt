package com.paranid5.crescendo.feature.playing.domain

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.PlaybackRepository.Companion.UNDEFINED_AUDIO_SESSION_ID
import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor
import com.paranid5.crescendo.system.services.stream.sendChangeRepeatBroadcast
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
    fun sendOnPrevButtonClickedBroadcast(audioStatus: AudioStatus) = audioStatus.fold(
        ifStream = streamServiceAccessor::sendSeekTo10SecsBackBroadcast,
        ifTrack = trackServiceInteractor::sendSwitchToPrevTrackBroadcast,
    )

    fun sendOnNextButtonClickedBroadcast(audioStatus: AudioStatus) = audioStatus.fold(
        ifStream = streamServiceAccessor::sendSeekTo10SecsForwardBroadcast,
        ifTrack = trackServiceInteractor::sendSwitchToNextTrackBroadcast,
    )

    fun sendSeekToBroadcast(audioStatus: AudioStatus, position: Long) = audioStatus.fold(
        ifStream = { streamServiceAccessor.sendSeekToBroadcast(position) },
        ifTrack = { trackServiceInteractor.sendSeekToBroadcast(position) },
    )

    fun sendSeekToLiveStreamRealPosition() =
        streamServiceAccessor.sendSeekToBroadcast(position = 0)

    fun sendPauseBroadcast(audioStatus: AudioStatus) = audioStatus.fold(
        ifStream = streamServiceAccessor::sendPauseBroadcast,
        ifTrack = trackServiceInteractor::sendPauseBroadcast,
    )

    fun startStreamingOrSendResumeBroadcast(audioStatus: AudioStatus) = audioStatus.fold(
        ifStream = streamServiceAccessor::startStreamingOrSendResumeBroadcast,
        ifTrack = trackServiceInteractor::startStreamingOrSendResumeBroadcast,
    )

    fun sendChangeRepeatBroadcast(audioStatus: AudioStatus) = audioStatus.fold(
        ifStream = streamServiceAccessor::sendChangeRepeatBroadcast,
        ifTrack = trackServiceInteractor::sendChangeRepeatBroadcast,
    )

    suspend fun updateSeekToPosition(audioStatus: AudioStatus, position: Long) = audioStatus.fold(
        ifStream = { playbackRepository.updateStreamPlaybackPosition(position = position) },
        ifTrack = { playbackRepository.updateTracksPlaybackPosition(position = position) },
    )

    val isAllowedToShowAudioEffects
        get() = playbackRepository.audioSessionIdState.value != UNDEFINED_AUDIO_SESSION_ID
}
