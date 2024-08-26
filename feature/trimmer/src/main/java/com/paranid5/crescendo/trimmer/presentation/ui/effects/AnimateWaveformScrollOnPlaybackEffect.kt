package com.paranid5.crescendo.trimmer.presentation.ui.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import arrow.core.raise.nullable
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerWaveformScrollState
import com.paranid5.crescendo.trimmer.view_model.TrimmerState

@Composable
internal fun AnimateWaveformScrollOnPlaybackEffect(
    state: TrimmerState,
    playbackPositionOffsetPx: Int,
) = nullable {
    val waveformScrollState = LocalTrimmerWaveformScrollState.current.bind()

    val isPlaying = remember(state.playbackProperties.isPlaying) {
        state.playbackProperties.isPlaying
    }

    val focusEvent = remember(state.focusEvent) {
        state.focusEvent
    }

    val waveformViewport = waveformScrollState.viewportSize

    LaunchedEffect(isPlaying, focusEvent, playbackPositionOffsetPx, waveformViewport) {
        if (isPlaying && focusEvent?.isFocused == true)
            waveformScrollState.animateScrollTo(
                maxOf(playbackPositionOffsetPx - waveformViewport / 2, 0)
            )
    }
}
