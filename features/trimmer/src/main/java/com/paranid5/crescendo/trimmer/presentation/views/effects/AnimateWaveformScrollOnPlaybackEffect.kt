package com.paranid5.crescendo.trimmer.presentation.views.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerWaveformScrollState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectFocusEventAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectIsPlayingAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AnimateWaveformScrollOnPlaybackEffect(
    playbackPositionOffsetPx: Int,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val waveformScrollState = LocalTrimmerWaveformScrollState.current!!

    val isPlaying by viewModel.collectIsPlayingAsState()
    val focusEvent by viewModel.collectFocusEventAsState()

    val waveformViewport = waveformScrollState.viewportSize

    LaunchedEffect(isPlaying, focusEvent, playbackPositionOffsetPx, waveformViewport) {
        if (isPlaying && focusEvent?.isFocused == true)
            waveformScrollState.animateScrollTo(
                maxOf(playbackPositionOffsetPx - waveformViewport / 2, 0)
            )
    }
}