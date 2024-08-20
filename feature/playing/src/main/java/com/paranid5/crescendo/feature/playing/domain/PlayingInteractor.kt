package com.paranid5.crescendo.feature.playing.domain

import com.paranid5.crescendo.core.common.PlaybackStatus
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
    fun sendOnPrevButtonClickedBroadcast(playbackStatus: PlaybackStatus) = playbackStatus.fold(
        ifStream = streamServiceAccessor::sendSeekTo10SecsBackBroadcast,
        ifTrack = trackServiceInteractor::sendSwitchToPrevTrackBroadcast,
    )

    fun sendOnNextButtonClickedBroadcast(playbackStatus: PlaybackStatus) = playbackStatus.fold(
        ifStream = streamServiceAccessor::sendSeekTo10SecsForwardBroadcast,
        ifTrack = trackServiceInteractor::sendSwitchToNextTrackBroadcast,
    )

    fun sendSeekToBroadcast(playbackStatus: PlaybackStatus, position: Long) = playbackStatus.fold(
        ifStream = { streamServiceAccessor.sendSeekToBroadcast(position) },
        ifTrack = { trackServiceInteractor.sendSeekToBroadcast(position) },
    )

    fun sendSeekToLiveStreamRealPosition() =
        streamServiceAccessor.sendSeekToBroadcast(position = 0)

    fun sendPauseBroadcast(playbackStatus: PlaybackStatus) = playbackStatus.fold(
        ifStream = streamServiceAccessor::sendPauseBroadcast,
        ifTrack = trackServiceInteractor::sendPauseBroadcast,
    )

    fun startStreamingOrSendResumeBroadcast(playbackStatus: PlaybackStatus) = playbackStatus.fold(
        ifStream = streamServiceAccessor::startStreamingOrSendResumeBroadcast,
        ifTrack = trackServiceInteractor::startStreamingOrSendResumeBroadcast,
    )

    fun sendChangeRepeatBroadcast(playbackStatus: PlaybackStatus) = playbackStatus.fold(
        ifStream = streamServiceAccessor::sendChangeRepeatBroadcast,
        ifTrack = trackServiceInteractor::sendChangeRepeatBroadcast,
    )

    suspend fun updateSeekToPosition(playbackStatus: PlaybackStatus, position: Long) = playbackStatus.fold(
        ifStream = { playbackRepository.updateStreamPlaybackPosition(position = position) },
        ifTrack = { playbackRepository.updateTracksPlaybackPosition(position = position) },
    )

    val isAllowedToShowAudioEffects
        get() = playbackRepository.audioSessionIdState.value != UNDEFINED_AUDIO_SESSION_ID
}
