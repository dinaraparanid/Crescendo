package com.paranid5.crescendo.playing.presentation.properties

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.playing.view_model.PlayingViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal suspend inline fun PlayingViewModel.setPlaybackPosition(
    audioStatus: AudioStatus,
    position: Long
) = when (audioStatus) {
    AudioStatus.STREAMING -> updateStreamPlaybackPosition(position)
    AudioStatus.PLAYING -> updateTracksPlaybackPosition(position)
}

internal inline val PlayingViewModel.streamDurationMillisFlow
    get() = currentMetadataFlow.map { it?.durationMillis ?: 0 }

internal inline val PlayingViewModel.tracksDurationMillisFlow
    get() = currentTrackFlow.map { it?.durationMillis ?: 0 }

internal fun PlayingViewModel.playbackPositionFlow(audioStatus: AudioStatus) =
    combine(
        streamPlaybackPositionFlow,
        tracksPlaybackPositionFlow
    ) { streamPos, tracksPos ->
        when (audioStatus) {
            AudioStatus.STREAMING -> streamPos
            AudioStatus.PLAYING -> tracksPos
        }
    }

internal fun PlayingViewModel.durationMillisFlow(audioStatus: AudioStatus) =
    combine(
        streamDurationMillisFlow,
        tracksDurationMillisFlow
    ) { streamDurationMillis, tracksDurationMillis ->
        when (audioStatus) {
            AudioStatus.STREAMING -> streamDurationMillis
            AudioStatus.PLAYING -> tracksDurationMillis
        }
    }