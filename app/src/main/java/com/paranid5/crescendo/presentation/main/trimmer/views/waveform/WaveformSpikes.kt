package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.DEFAULT_GRAPHICS_LAYER_ALPHA
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.endOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.startOffsetFlow
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun WaveformSpikes(
    canvasSizeState: MutableState<Size>,
    spikesState: MutableFloatState,
    spikesAmplitudesState: State<List<Float>>,
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val waveformBrush = SolidColor(colors.backgroundAlternative)
    val progressWaveformBrush = SolidColor(colors.secondary)

    val startOffset by viewModel.startOffsetFlow.collectAsState(initial = 0F)
    val endOffset by viewModel.endOffsetFlow.collectAsState(initial = 0F)

    var canvasSize by canvasSizeState
    var spikes by spikesState
    val spikesAmplitudes by spikesAmplitudesState

    Canvas(modifier.graphicsLayer(alpha = DEFAULT_GRAPHICS_LAYER_ALPHA)) {
        canvasSize = size
        spikes = size.width

        println("Upd size: $canvasSize; Upd spikes: $spikes")

        spikesAmplitudes.forEachIndexed { index, amplitude ->
            drawSpike(index, amplitude, waveformBrush)
        }

        highlightProgressWaveform(
            startPoint = startOffset,
            endPoint = endOffset,
            progressWaveformBrush = progressWaveformBrush
        )
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