package com.paranid5.crescendo.presentation.main.playing.properties

import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

suspend inline fun PlayingViewModel.setPlaybackPosition(
    audioStatus: AudioStatus,
    position: Long
) = when (audioStatus) {
    AudioStatus.STREAMING -> setStreamPlaybackPosition(position)
    AudioStatus.PLAYING -> setTracksPlaybackPosition(position)
}

inline val PlayingViewModel.streamDurationMillisFlow
    get() = currentMetadataFlow.map { it?.durationMillis ?: 0 }

inline val PlayingViewModel.tracksDurationMillisFlow
    get() = currentTrackFlow.map { it?.durationMillis ?: 0 }

fun PlayingViewModel.playbackPositionFlow(audioStatus: AudioStatus) =
    combine(
        streamPlaybackPositionFlow,
        tracksPlaybackPositionFlow
    ) { streamPos, tracksPos ->
        when (audioStatus) {
            AudioStatus.STREAMING -> streamPos
            AudioStatus.PLAYING -> tracksPos
        }
    }

fun PlayingViewModel.durationMillisFlow(audioStatus: AudioStatus) =
    combine(
        streamDurationMillisFlow,
        tracksDurationMillisFlow
    ) { streamDurationMillis, tracksDurationMillis ->
        when (audioStatus) {
            AudioStatus.STREAMING -> streamDurationMillis
            AudioStatus.PLAYING -> tracksDurationMillis
        }
    }