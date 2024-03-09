package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import com.paranid5.crescendo.utils.extensions.forEachIndexedStepped
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.core.impl.presentation.composition_locals.trimmer.LocalTrimmerFocusPoints
import com.paranid5.crescendo.core.impl.presentation.composition_locals.trimmer.LocalTrimmerWaveformScrollState
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.DEFAULT_GRAPHICS_LAYER_ALPHA
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectEndOffsetAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectIsPlayingAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectStartOffsetAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectWaveformWidthAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectZoomAsState
import com.paranid5.crescendo.utils.extensions.pxToDp
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@Composable
fun WaveformSpikes(
    spikesAmplitudes: List<Float>,
    canvasSizeState: MutableState<Size>,
    spikesState: MutableFloatState,
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinActivityViewModel(),
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO
) {
    val colors = LocalAppColors.current
    val waveformBrush = SolidColor(colors.backgroundAlternative)
    val progressWaveformBrush = SolidColor(colors.secondary)

    val startOffset by viewModel.collectStartOffsetAsState()
    val endOffset by viewModel.collectEndOffsetAsState()
    val isPlaying by viewModel.collectIsPlayingAsState()
    val zoom by viewModel.collectZoomAsState()

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

    if (!isPlaying)
        BorderSeekers(spikeWidthRatio)
}

@Composable
private fun BorderSeekers(
    spikeWidthRatio: Int,
    viewModel: TrimmerViewModel = koinActivityViewModel(),
) {
    val waveformWidth by viewModel.collectWaveformWidthAsState(spikeWidthRatio)
    val startBorderOffset by rememberStartBorderOffsetAsState(waveformWidth)
    val endBorderOffset by rememberEndBorderOffsetAsState(waveformWidth)

    val waveformScrollState = LocalTrimmerWaveformScrollState.current!!

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
                endBorderOffset = endBorderOffset
            )

        waveformPositionDp.value.toInt() in startBorderOffset..endBorderOffset ->
            SBSBeforeEBSAfter(
                waveformPosition = waveformPosition,
                waveformPositionDp = waveformPositionDp,
                waveformViewport = waveformViewport,
                waveformViewportDp = waveformViewportDp,
                startBorderOffset = startBorderOffset,
                endBorderOffset = endBorderOffset
            )

        else -> EBSBefore(
            waveformPosition = waveformPosition,
            waveformPositionDp = waveformPositionDp,
            startBorderOffset = startBorderOffset
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
    endBorderOffset: Int
) {
    val controllerCircleRadiusDp = CONTROLLER_CIRCLE_RADIUS.toInt().pxToDp()
    val controllerCircleRadius = controllerCircleRadiusDp.value

    when {
        waveformPosition + waveformViewport + controllerCircleRadius * 2 < startBorderOffset ->
            StartBorderSeeker(
                Modifier
                    .fillMaxHeight()
                    .offset(x = waveformPositionDp + waveformViewportDp - controllerCircleRadiusDp * 2)
            )

        waveformPosition + waveformViewport + controllerCircleRadius * 2 < endBorderOffset ->
            EndBorderSeeker(
                Modifier
                    .fillMaxHeight()
                    .offset(x = waveformPositionDp + waveformViewportDp - controllerCircleRadiusDp)
            )
    }
}

@Composable
private fun SBSBeforeEBSAfter(
    waveformPosition: Float,
    waveformPositionDp: Dp,
    waveformViewport: Float,
    waveformViewportDp: Dp,
    startBorderOffset: Int,
    endBorderOffset: Int
) {
    val controllerCircleRadiusDp = CONTROLLER_CIRCLE_RADIUS.toInt().pxToDp()
    val controllerCircleRadius = controllerCircleRadiusDp.value

    if (waveformPosition - controllerCircleRadius * 2 > startBorderOffset)
        StartBorderSeeker(
            Modifier
                .fillMaxHeight()
                .offset(x = waveformPositionDp + controllerCircleRadiusDp)
        )

    if (waveformPosition + waveformViewport + controllerCircleRadius * 2 < endBorderOffset)
        EndBorderSeeker(
            Modifier
                .fillMaxHeight()
                .offset(x = waveformPositionDp + waveformViewportDp - controllerCircleRadiusDp)
        )
}

@Composable
private fun EBSBefore(
    waveformPosition: Float,
    waveformPositionDp: Dp,
    startBorderOffset: Int
) {
    val controllerCircleRadiusDp = CONTROLLER_CIRCLE_RADIUS.toInt().pxToDp()
    val controllerCircleRadius = controllerCircleRadiusDp.value

    if (waveformPosition - controllerCircleRadius * 2 > startBorderOffset)
        EndBorderSeeker(
            Modifier
                .fillMaxHeight()
                .offset(x = waveformPositionDp + controllerCircleRadiusDp * 2)
        )
}

@Composable
private fun StartBorderSeeker(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val focusPoints = LocalTrimmerFocusPoints.current!!

    val progressBrush = SolidColor(colors.primary)
    val iconBrush = SolidColor(colors.fontColor)

    Canvas(
        modifier.clickable {
            focusPoints
                .startBorderFocusRequester
                .requestFocus()
        }
    ) {
        drawStartToucher(progressBrush, iconBrush)
    }
}

@Composable
private fun EndBorderSeeker(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val focusPoints = LocalTrimmerFocusPoints.current!!

    val progressBrush = SolidColor(colors.primary)
    val iconBrush = SolidColor(colors.fontColor)

    Canvas(
        modifier.clickable {
            focusPoints
                .endBorderFocusRequester
                .requestFocus()
        }
    ) {
        drawEndToucher(progressBrush, iconBrush)
    }
}

private fun DrawScope.drawSpike(index: Int, amplitude: Float, waveformBrush: SolidColor) =
    drawRect(
        brush = waveformBrush,
        topLeft = Offset(
            x = index.toFloat(),
            y = (size.height - CONTROLLER_HEIGHT_OFFSET) / 2F - amplitude / 2F
        ),
        size = Size(
            width = 1F,
            height = amplitude
        ),
        style = Fill
    )

private fun DrawScope.highlightProgressWaveform(
    startPoint: Float,
    endPoint: Float,
    progressWaveformBrush: SolidColor
) = drawRect(
    brush = progressWaveformBrush,
    topLeft = Offset(startPoint * size.width, 0F),
    size = Size(
        width = (endPoint - startPoint) * size.width,
        height = size.height - CONTROLLER_HEIGHT_OFFSET
    ),
    blendMode = BlendMode.SrcAtop
)