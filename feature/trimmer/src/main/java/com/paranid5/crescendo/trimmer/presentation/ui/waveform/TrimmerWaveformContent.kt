package com.paranid5.crescendo.trimmer.presentation.ui.waveform

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
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
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_PADDING
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerFocusPoints
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerWaveformScrollState
import com.paranid5.crescendo.trimmer.presentation.ui.effects.AnimateWaveformScrollOnPlaybackEffect
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.utils.extensions.pxToDp
import com.paranid5.crescendo.utils.extensions.toPx
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
internal fun TrimmerWaveformContent(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    spikesAmplitudes: ImmutableList<Float>,
    canvasSizeState: MutableState<Size>,
    spikesState: MutableFloatState,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    val waveformWidth by remember(state, spikeWidthRatio) {
        derivedStateOf { state.waveformWidth(spikeWidthRatio) }
    }

    Box(modifier) {
        Waveform(
            state = state,
            onUiIntent = onUiIntent,
            spikeWidthRatio = spikeWidthRatio,
            canvasSizeState = canvasSizeState,
            spikesState = spikesState,
            spikesAmplitudes = spikesAmplitudes,
            modifier = Modifier.waveformModifier(waveformWidth = waveformWidth),
        )

        StartBorder(
            state = state,
            onUiIntent = onUiIntent,
            spikeWidthRatio = spikeWidthRatio,
            modifier = Modifier.startBorderModifier(
                state = state,
                waveformWidth = waveformWidth,
            ),
        )

        EndBorder(
            state = state,
            onUiIntent = onUiIntent,
            spikeWidthRatio = spikeWidthRatio,
            modifier = Modifier.endBorderModifier(
                state = state,
                waveformWidth = waveformWidth,
            ),
        )

        PlaybackPosition(
            modifier = Modifier.playbackOffsetModifier(
                state = state,
                onUiIntent = onUiIntent,
                waveformWidth = waveformWidth,
            ),
        )
    }
}

@Composable
private fun Modifier.waveformModifier(waveformWidth: Int) =
    this
        .width(waveformWidth.dp)
        .padding(horizontal = WAVEFORM_PADDING.pxToDp())
        .fillMaxHeight()

@Composable
private fun Modifier.startBorderModifier(
    state: TrimmerState,
    waveformWidth: Int,
): Modifier {
    val focusPoints = LocalTrimmerFocusPoints.current ?: return Modifier
    val waveformScrollState = LocalTrimmerWaveformScrollState.current ?: return Modifier
    val coroutineScope = rememberCoroutineScope()

    val startBorderOffset by rememberStartBorderOffsetAsState(
        state = state,
        waveformWidth = waveformWidth,
    )

    val controllerCircleRadiusPx = CONTROLLER_CIRCLE_RADIUS.dp.toPx().toInt()
    val startBorderOffsetPx = startBorderOffset.dp.toPx().toInt()

    return this
        .offset(x = startBorderOffset.dp)
        .fillMaxHeight()
        .zIndex(10F)
        .focusRequester(focusPoints.startBorderFocusRequester)
        .onFocusEvent {
            if (it.isFocused)
                coroutineScope.launch {
                    waveformScrollState.animateScrollTo(
                        startBorderOffsetPx - controllerCircleRadiusPx
                    )
                }
        }
        .focusable()
}

@Composable
private fun Modifier.endBorderModifier(
    state: TrimmerState,
    waveformWidth: Int,
): Modifier {
    val focusPoints = LocalTrimmerFocusPoints.current ?: return Modifier
    val waveformScrollState = LocalTrimmerWaveformScrollState.current ?: return Modifier
    val coroutineScope = rememberCoroutineScope()

    val endBorderOffset by rememberEndBorderOffsetAsState(
        state = state,
        waveformWidth = waveformWidth
    )

    val controllerCircleRadiusPx = CONTROLLER_CIRCLE_RADIUS.dp.toPx().toInt()
    val endBorderOffsetPx = endBorderOffset.dp.toPx().toInt()
    val waveformViewport = waveformScrollState.viewportSize

    return this
        .offset(x = endBorderOffset.dp)
        .fillMaxHeight()
        .zIndex(10F)
        .focusRequester(focusPoints.endBorderFocusRequester)
        .onFocusEvent {
            if (it.isFocused)
                coroutineScope.launch {
                    waveformScrollState.animateScrollTo(
                        endBorderOffsetPx - waveformViewport + controllerCircleRadiusPx
                    )
                }
        }
        .focusable()
}

@Composable
private fun Modifier.playbackOffsetModifier(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    waveformWidth: Int,
): Modifier {
    val focusPoints = LocalTrimmerFocusPoints.current ?: return Modifier

    val playbackPositionOffset by rememberPlaybackPositionOffsetAsState(
        state = state,
        waveformWidth = waveformWidth
    )

    val playbackPositionOffsetPx = playbackPositionOffset.dp.toPx().toInt()
    val playbackAlphaAnim by animatePlaybackAlphaAsState(state = state)

    AnimateWaveformScrollOnPlaybackEffect(
        state = state,
        playbackPositionOffsetPx = playbackPositionOffsetPx,
    )

    return this
        .offset(x = playbackPositionOffset.dp)
        .alpha(playbackAlphaAnim)
        .fillMaxHeight()
        .zIndex(8F)
        .focusRequester(focusPoints.playbackFocusRequester)
        .onFocusEvent { onUiIntent(TrimmerUiIntent.UpdateFocusEvent(focusEvent = it)) }
        .focusTarget()
}

@Composable
internal fun rememberStartBorderOffsetAsState(
    state: TrimmerState,
    waveformWidth: Int,
): State<Int> {
    val startOffset = remember(state.startOffset) { state.startOffset }

    return remember(startOffset, waveformWidth) {
        derivedStateOf { StartBorderOffset(startOffset, waveformWidth) }
    }
}

@Composable
internal fun rememberEndBorderOffsetAsState(
    state: TrimmerState,
    waveformWidth: Int,
): State<Int> {
    val endOffset = remember(state.endOffset) { state.endOffset }

    return remember(endOffset, waveformWidth) {
        derivedStateOf { EndBorderOffset(endOffset, waveformWidth) }
    }
}

@Composable
private fun rememberPlaybackPositionOffsetAsState(
    state: TrimmerState,
    waveformWidth: Int,
): State<Int> {
    val playbackOffset = remember(state.playbackOffset) { state.playbackOffset }

    return remember(playbackOffset, waveformWidth) {
        derivedStateOf { PlaybackPositionOffset(playbackOffset, waveformWidth) }
    }
}

@Composable
private fun animatePlaybackAlphaAsState(state: TrimmerState): State<Float> {
    val playbackAlpha = remember(state.playbackProperties.playbackAlpha) {
        state.playbackProperties.playbackAlpha
    }

    return animateFloatAsState(playbackAlpha, label = "")
}
