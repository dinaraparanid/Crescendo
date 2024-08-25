package com.paranid5.crescendo.trimmer.presentation.ui.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.trimmer.presentation.DEFAULT_GRAPHICS_LAYER_ALPHA
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerFocusPoints
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerWaveformScrollState
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import com.paranid5.crescendo.utils.extensions.forEachIndexedStepped
import com.paranid5.crescendo.utils.extensions.pxToDp
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun WaveformSpikes(
    state: TrimmerState,
    spikesAmplitudes: ImmutableList<Float>,
    canvasSizeState: MutableState<Size>,
    spikesState: MutableFloatState,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    val appColors = colors
    val waveformBrush = remember(appColors) { SolidColor(appColors.background.alternative) }
    val progressWaveformBrush = remember(appColors) { SolidColor(appColors.secondary) }

    val startOffset = remember(state.startOffset) { state.startOffset }
    val endOffset = remember(state.endOffset) { state.endOffset }

    val isPlaying = remember(state.playbackProperties.isPlaying) {
        state.playbackProperties.isPlaying
    }

    val zoom = remember(state.waveformProperties.zoomLevel) {
        state.waveformProperties.zoomLevel
    }

    var canvasSize by canvasSizeState
    var spikes by spikesState

    Canvas(modifier.graphicsLayer(alpha = DEFAULT_GRAPHICS_LAYER_ALPHA)) {
        canvasSize = size
        spikes = size.width

        spikesAmplitudes.forEachIndexedStepped(step = zoom + 1) { index, amplitude ->
            drawSpike(index, amplitude, waveformBrush)
        }

        highlightProgressWaveform(
            startPoint = startOffset,
            endPoint = endOffset,
            progressWaveformBrush = progressWaveformBrush
        )
    }

    if (isPlaying.not())
        BorderSeekers(
            state = state,
            spikeWidthRatio = spikeWidthRatio,
        )
}

@Composable
private fun BorderSeekers(
    state: TrimmerState,
    spikeWidthRatio: Int,
) = nullable {
    val waveformScrollState = LocalTrimmerWaveformScrollState.current.bind()

    val waveformWidth by remember(state, spikeWidthRatio) {
        derivedStateOf { state.waveformWidth(spikeWidthRatio) }
    }

    val startBorderOffset by rememberStartBorderOffsetAsState(
        state = state,
        waveformWidth = waveformWidth,
    )

    val endBorderOffset by rememberEndBorderOffsetAsState(
        state = state,
        waveformWidth = waveformWidth,
    )

    val waveformPositionDp = waveformScrollState.value.pxToDp()
    val waveformPosition = waveformPositionDp.value

    val waveformViewportDp = waveformScrollState.viewportSize.pxToDp()
    val waveformViewport = waveformViewportDp.value

    when {
        waveformPosition < startBorderOffset ->
            SBSOrEBSAfter(
                waveformPosition = waveformPosition,
                waveformPositionDp = waveformPositionDp,
                waveformViewport = waveformViewport,
                waveformViewportDp = waveformViewportDp,
                startBorderOffset = startBorderOffset,
                endBorderOffset = endBorderOffset,
            )

        waveformPositionDp.value.toInt() in startBorderOffset..endBorderOffset ->
            SBSBeforeEBSAfter(
                waveformPosition = waveformPosition,
                waveformPositionDp = waveformPositionDp,
                waveformViewport = waveformViewport,
                waveformViewportDp = waveformViewportDp,
                startBorderOffset = startBorderOffset,
                endBorderOffset = endBorderOffset,
            )

        else -> EBSBefore(
            waveformPosition = waveformPosition,
            waveformPositionDp = waveformPositionDp,
            startBorderOffset = startBorderOffset,
        )
    }
}

@Composable
private fun SBSOrEBSAfter(
    waveformPosition: Float,
    waveformPositionDp: Dp,
    waveformViewport: Float,
    waveformViewportDp: Dp,
    startBorderOffset: Int,
    endBorderOffset: Int,
) {
    val controllerCircleRadiusDp = CONTROLLER_CIRCLE_RADIUS.toInt().pxToDp()
    val controllerCircleRadius = controllerCircleRadiusDp.value

    val controllerPosition by remember(
        waveformPosition,
        waveformViewport,
        controllerCircleRadius,
    ) {
        derivedStateOf { waveformPosition + waveformViewport + controllerCircleRadius * 2 }
    }

    val isStartBorderSeekerShown by remember(controllerPosition, startBorderOffset) {
        derivedStateOf { controllerPosition < startBorderOffset }
    }

    val isEndBorderSeekerShown by remember(controllerPosition, endBorderOffset) {
        derivedStateOf { controllerPosition < endBorderOffset }
    }

    @Composable
    fun SBS() {
        val offset by remember(
            waveformPositionDp,
            waveformViewportDp,
            controllerCircleRadiusDp,
        ) {
            derivedStateOf {
                waveformPositionDp + waveformViewportDp - controllerCircleRadiusDp * 2
            }
        }

        StartBorderSeeker(
            Modifier
                .fillMaxHeight()
                .offset(x = offset)
        )
    }

    @Composable
    fun EBS() {
        val offset by remember(
            waveformPositionDp,
            waveformViewportDp,
            controllerCircleRadiusDp,
        ) {
            derivedStateOf {
                waveformPositionDp + waveformViewportDp - controllerCircleRadiusDp
            }
        }

        EndBorderSeeker(
            Modifier
                .fillMaxHeight()
                .offset(x = offset)
        )
    }

    when {
        isStartBorderSeekerShown -> SBS()
        isEndBorderSeekerShown -> EBS()
    }
}

@Composable
private fun SBSBeforeEBSAfter(
    waveformPosition: Float,
    waveformPositionDp: Dp,
    waveformViewport: Float,
    waveformViewportDp: Dp,
    startBorderOffset: Int,
    endBorderOffset: Int,
) {
    val controllerCircleRadiusDp = CONTROLLER_CIRCLE_RADIUS.toInt().pxToDp()
    val controllerCircleRadius = controllerCircleRadiusDp.value

    val isStartBorderSeekerShown by remember(
        waveformPosition,
        controllerCircleRadius,
        startBorderOffset,
    ) {
        derivedStateOf {
            waveformPosition - controllerCircleRadius * 2 > startBorderOffset
        }
    }

    val isEndBorderSeekerShown by remember(
        waveformPosition,
        waveformViewport,
        controllerCircleRadius,
        endBorderOffset,
    ) {
        derivedStateOf {
            waveformPosition + waveformViewport + controllerCircleRadius * 2 < endBorderOffset
        }
    }

    @Composable
    fun SBS() {
        val offset = remember(waveformPositionDp, controllerCircleRadiusDp) {
            waveformPositionDp + controllerCircleRadiusDp
        }

        StartBorderSeeker(
            Modifier
                .fillMaxHeight()
                .offset(x = offset)
        )
    }

    @Composable
    fun EBS() {
        val offset by remember(
            waveformPositionDp,
            waveformViewportDp,
            controllerCircleRadiusDp,
        ) {
            derivedStateOf {
                waveformPositionDp + waveformViewportDp - controllerCircleRadiusDp
            }
        }

        EndBorderSeeker(
            Modifier
                .fillMaxHeight()
                .offset(x = offset)
        )
    }

    if (isStartBorderSeekerShown) SBS()
    if (isEndBorderSeekerShown) EBS()
}

@Composable
private fun EBSBefore(
    waveformPosition: Float,
    waveformPositionDp: Dp,
    startBorderOffset: Int,
) {
    val controllerCircleRadiusDp = CONTROLLER_CIRCLE_RADIUS.toInt().pxToDp()
    val controllerCircleRadius = controllerCircleRadiusDp.value

    val isEndBorderSeekerShown by remember(
        waveformPosition,
        controllerCircleRadius,
        startBorderOffset,
    ) {
        derivedStateOf { waveformPosition - controllerCircleRadius * 2 > startBorderOffset }
    }

    if (isEndBorderSeekerShown) {
        val offset by remember(waveformPositionDp, controllerCircleRadiusDp) {
            derivedStateOf { waveformPositionDp + controllerCircleRadiusDp * 2 }
        }

        EndBorderSeeker(
            Modifier
                .fillMaxHeight()
                .offset(x = offset)
        )
    }
}

@Composable
private fun StartBorderSeeker(modifier: Modifier = Modifier) = nullable {
    val focusPoints = LocalTrimmerFocusPoints.current.bind()

    val appColors = colors
    val progressBrush = remember(appColors) { SolidColor(appColors.primary) }
    val iconBrush = remember(appColors) { SolidColor(appColors.text.primary) }

    Canvas(
        modifier.clickableWithRipple(bounded = true) {
            focusPoints
                .startBorderFocusRequester
                .requestFocus()
        }
    ) {
        drawStartTouchPad(progressBrush, iconBrush)
    }
}

@Composable
private fun EndBorderSeeker(modifier: Modifier = Modifier) = nullable {
    val focusPoints = LocalTrimmerFocusPoints.current.bind()

    val appColors = colors
    val progressBrush = remember(appColors) { SolidColor(appColors.primary) }
    val iconBrush = remember(appColors) { SolidColor(appColors.text.primary) }

    Canvas(
        modifier.clickableWithRipple(bounded = true) {
            focusPoints
                .endBorderFocusRequester
                .requestFocus()
        }
    ) {
        drawEndTouchPad(progressBrush, iconBrush)
    }
}

private fun DrawScope.drawSpike(
    index: Int,
    amplitude: Float,
    waveformBrush: SolidColor,
) = drawRect(
    brush = waveformBrush,
    topLeft = Offset(
        x = index.toFloat(),
        y = (size.height - CONTROLLER_HEIGHT_OFFSET) / 2F - amplitude / 2F,
    ),
    size = Size(
        width = 1F,
        height = amplitude,
    ),
    style = Fill,
)

private fun DrawScope.highlightProgressWaveform(
    startPoint: Float,
    endPoint: Float,
    progressWaveformBrush: SolidColor,
) = drawRect(
    brush = progressWaveformBrush,
    topLeft = Offset(startPoint * size.width, 0F),
    size = Size(
        width = (endPoint - startPoint) * size.width,
        height = size.height - CONTROLLER_HEIGHT_OFFSET
    ),
    blendMode = BlendMode.SrcAtop,
)
