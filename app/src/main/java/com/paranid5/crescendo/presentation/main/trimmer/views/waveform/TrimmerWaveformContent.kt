package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.paranid5.crescendo.presentation.composition_locals.LocalTrimmerFocusPoints
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_PADDING
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.presentation.main.trimmer.properties.endOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.isPlayingState
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackAlphaFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.startOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.waveformWidthFlow
import com.paranid5.crescendo.presentation.ui.extensions.pxToDp
import kotlinx.coroutines.launch

@Composable
fun TrimmerWaveformContent(
    viewModel: TrimmerViewModel,
    waveformScrollState: ScrollState,
    canvasSizeState: MutableState<Size>,
    spikesState: MutableFloatState,
    spikesAmplitudesState: State<List<Float>>,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO
) {
    val waveformWidth by collectWaveformWidthAsState(viewModel, spikeWidthRatio)

    Box(modifier) {
        Waveform(
            viewModel = viewModel,
            canvasSizeState = canvasSizeState,
            spikesState = spikesState,
            spikesAmplitudesState = spikesAmplitudesState,
            modifier = Modifier.waveformModifier(waveformWidth)
        )

        StartBorder(
            viewModel = viewModel,
            spikeWidthRatio = spikeWidthRatio,
            modifier = Modifier.startBorderModifier(
                viewModel = viewModel,
                waveformWidth = waveformWidth,
                waveformScrollState = waveformScrollState
            )
        )

        EndBorder(
            viewModel = viewModel,
            spikeWidthRatio = spikeWidthRatio,
            modifier = Modifier.endBorderModifier(
                viewModel = viewModel,
                waveformWidth = waveformWidth,
                waveformScrollState = waveformScrollState
            )
        )

        PlaybackPosition(
            modifier = Modifier.playbackOffsetModifier(
                viewModel = viewModel,
                waveformWidth = waveformWidth,
                waveformScrollState = waveformScrollState
            )
        )
    }
}

@Composable
private fun collectWaveformWidthAsState(
    viewModel: TrimmerViewModel,
    spikeWidthRatio: Int
) = viewModel
    .waveformWidthFlow(spikeWidthRatio)
    .collectAsState(initial = 0)

@Composable
private fun Modifier.waveformModifier(waveformWidth: Int) =
    this
        .width(waveformWidth.dp)
        .padding(horizontal = WAVEFORM_PADDING.pxToDp())
        .fillMaxHeight()

@Composable
private fun Modifier.startBorderModifier(
    viewModel: TrimmerViewModel,
    waveformWidth: Int,
    waveformScrollState: ScrollState
): Modifier {
    val focusPoints = LocalTrimmerFocusPoints.current!!
    val coroutineScope = rememberCoroutineScope()

    val startBorderOffset by rememberStartBorderOffsetAsState(
        viewModel = viewModel,
        waveformWidth = waveformWidth
    )

    return this
        .offset(x = startBorderOffset.dp)
        .fillMaxHeight()
        .zIndex(10F)
        .focusRequester(focusPoints.startBorderFocusRequester)
        .onFocusEvent {
            if (it.isFocused)
                coroutineScope.launch {
                    waveformScrollState.animateScrollTo(startBorderOffset)
                    focusPoints.startBorderFocusRequester.freeFocus()
                }
        }
        .focusTarget()
}

@Composable
private fun Modifier.endBorderModifier(
    viewModel: TrimmerViewModel,
    waveformWidth: Int,
    waveformScrollState: ScrollState
): Modifier {
    val focusPoints = LocalTrimmerFocusPoints.current!!
    val coroutineScope = rememberCoroutineScope()

    val endBorderOffset by rememberEndBorderOffsetAsState(
        viewModel = viewModel,
        waveformWidth = waveformWidth
    )

    return this
        .offset(x = endBorderOffset.dp)
        .fillMaxHeight()
        .zIndex(10F)
        .focusRequester(focusPoints.endBorderFocusRequester)
        .onFocusEvent {
            if (it.isFocused)
                coroutineScope.launch {
                    waveformScrollState.animateScrollTo(endBorderOffset)
                    focusPoints.endBorderFocusRequester.freeFocus()
                }
        }
        .focusTarget()
}

@Composable
private fun Modifier.playbackOffsetModifier(
    viewModel: TrimmerViewModel,
    waveformWidth: Int,
    waveformScrollState: ScrollState
): Modifier {
    val focusPoints = LocalTrimmerFocusPoints.current!!
    val isPlaying by viewModel.isPlayingState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val playbackPositionOffset by rememberPlaybackPositionOffsetAsState(
        viewModel = viewModel,
        waveformWidth = waveformWidth
    )

    val playbackAlphaAnim by animatePlaybackAlphaAsState(viewModel)

    return this
        .offset(x = playbackPositionOffset.dp)
        .alpha(playbackAlphaAnim)
        .fillMaxHeight()
        .zIndex(8F)
        .focusRequester(focusPoints.playbackFocusRequester)
        .onFocusEvent {
            if (isPlaying && it.isFocused)
                coroutineScope.launch {
                    waveformScrollState.animateScrollTo(playbackPositionOffset)
                    focusPoints.playbackFocusRequester.freeFocus()
                }
        }
        .focusTarget()
}

@Composable
private fun rememberStartBorderOffsetAsState(
    viewModel: TrimmerViewModel,
    waveformWidth: Int,
): State<Int> {
    val startOffset by viewModel.startOffsetFlow.collectAsState(initial = 0F)

    return remember(startOffset, waveformWidth) {
        derivedStateOf { StartBorderOffset(startOffset, waveformWidth) }
    }
}

@Composable
private fun rememberEndBorderOffsetAsState(
    viewModel: TrimmerViewModel,
    waveformWidth: Int,
): State<Int> {
    val endOffset by viewModel.endOffsetFlow.collectAsState(initial = 0F)

    return remember(endOffset, waveformWidth) {
        derivedStateOf { EndBorderOffset(endOffset, waveformWidth) }
    }
}

@Composable
private fun rememberPlaybackPositionOffsetAsState(
    viewModel: TrimmerViewModel,
    waveformWidth: Int,
): State<Int> {
    val playbackOffset by viewModel.playbackOffsetFlow.collectAsState(initial = 0F)

    return remember(playbackOffset, waveformWidth) {
        derivedStateOf { PlaybackPositionOffset(playbackOffset, waveformWidth) }
    }
}

@Composable
private fun animatePlaybackAlphaAsState(viewModel: TrimmerViewModel): State<Float> {
    val playbackAlpha by viewModel.playbackAlphaFlow.collectAsState(initial = 0F)
    return animateFloatAsState(playbackAlpha, label = "")
}