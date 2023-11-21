package com.paranid5.crescendo.presentation.trimmer

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.theme.TransparentUtility
import com.paranid5.crescendo.presentation.ui.utils.ext.pxToDp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.callback.AmplitudaErrorListener
import kotlin.math.ceil
import kotlin.math.roundToInt

private const val MinSpikeHeight = 1F
private const val DefaultGraphicsLayerAlpha = 0.99F

@Composable
fun TrimWaveform(
    model: String?,
    durationInMillis: Long,
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier,
    spikeAnimationSpec: AnimationSpec<Float> = tween(500),
    spikeWidthRatio: Int = 5
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current.value

    val waveformBrush = SolidColor(TransparentUtility)
    val progressBrush = SolidColor(colors.primary)

    val amplituda by remember { derivedStateOf { Amplituda(context) } }
    val amplitudes by viewModel.amplitudesState.collectAsState()
    var drawableAmplitudes by remember { mutableStateOf(listOf<Float>()) }

    val startMillis by viewModel.startPosInMillisState.collectAsState()
    val offset by remember { derivedStateOf { startMillis safeDiv durationInMillis } }

    val endMillis by viewModel.endPosInMillisState.collectAsState()
    val endPoint by remember { derivedStateOf { endMillis safeDiv durationInMillis } }

    var canvasSize by remember { mutableStateOf(Size(0F, 0F)) }
    var spikes by remember { mutableFloatStateOf(0F) }

    val spikesAmplitudes = drawableAmplitudes.map {
        animateFloatAsState(it, spikeAnimationSpec, label = "").value
    }

    val canvasWidth = durationInMillis / 1000 * spikeWidthRatio
    val canvasWidthDp = (durationInMillis / 1000 * spikeWidthRatio).toInt().dp

    LaunchedEffect(key1 = model) {
        withContext(Dispatchers.IO) {
            if (model != null) viewModel.setAmplitudes(
                amplituda
                    .processAudio(model)
                    .get(AmplitudaErrorListener { it.printStackTrace() })
                    .amplitudesAsList()
            )
        }
    }

    LaunchedEffect(key1 = amplitudes) {
        withContext(Dispatchers.IO) {
            drawableAmplitudes = amplitudes.toDrawableAmplitudes(
                spikes = spikes.toInt(),
                minHeight = MinSpikeHeight,
                maxHeight = canvasSize.height.coerceAtLeast(MinSpikeHeight)
            )
        }
    }

    Box(modifier) {
        Canvas(
            Modifier
                .width(canvasWidthDp)
                .padding(horizontal = 25.pxToDp())
                .fillMaxHeight()
                .graphicsLayer(alpha = DefaultGraphicsLayerAlpha)
        ) {
            canvasSize = size
            spikes = size.width

            spikesAmplitudes.forEachIndexed { index, amplitude ->
                drawRect(
                    brush = waveformBrush,
                    topLeft = Offset(
                        x = index.toFloat(),
                        y = size.height / 2F - amplitude / 2F
                    ),
                    size = Size(
                        width = 1F,
                        height = amplitude
                    ),
                    style = Fill
                )

                drawRect(
                    brush = progressBrush,
                    topLeft = Offset(offset * size.width, 0F),
                    size = Size(
                        width = (endPoint - offset) * size.width,
                        height = size.height
                    ),
                    blendMode = BlendMode.SrcAtop
                )
            }
        }

        Canvas(
            Modifier
                .offset(x = (offset * canvasWidth).toInt().dp)
                .fillMaxHeight()
                .zIndex(10F)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.setStartPosInMillis(
                            (startMillis + (dragAmount * 200 / spikeWidthRatio))
                                .toLong()
                                .coerceAtLeast(0)
                        )
                    }
                }
        ) {
            drawRoundRect(
                color = colors.primary,
                topLeft = Offset(25F - 7F, 0F),
                size = Size(
                    width = 15F,
                    height = size.height
                ),
                cornerRadius = CornerRadius(10F, 10F),
                style = Fill
            )

            drawCircle(
                brush = progressBrush,
                radius = 25F,
                center = Offset(25F, size.height - 16F)
            )

            drawPath(
                path = Path().apply {
                    moveTo(25F + 8F, size.height - 16F - 12F)
                    lineTo(25F - 10F, size.height - 16F)
                    lineTo(25F + 8F, size.height - 16F + 12F)
                    close()
                },
                color = colors.inverseSurface,
                style = Fill
            )
        }

        Canvas(
            Modifier
                .offset(x = (endPoint * canvasWidth).toInt().dp)
                .fillMaxHeight()
                .zIndex(10F)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.setEndPosInMillis(
                            (endMillis + (dragAmount * 200 / spikeWidthRatio))
                                .toLong()
                                .coerceIn(startMillis + 1..durationInMillis)
                        )
                    }
                }
        ) {
            drawRoundRect(
                color = colors.primary,
                topLeft = Offset(-25F - 7F, 0F),
                size = Size(
                    width = 15F,
                    height = size.height
                ),
                cornerRadius = CornerRadius(10F, 10F),
                style = Fill
            )

            drawCircle(
                brush = progressBrush,
                radius = 25F,
                center = Offset(-25F, size.height - 16F)
            )

            drawPath(
                path = Path().apply {
                    moveTo(-25F - 8F, size.height - 16F - 12F)
                    lineTo(-25F + 10F, size.height - 16F)
                    lineTo(-25F - 8F, size.height - 16F + 12F)
                    close()
                },
                color = colors.inverseSurface,
                style = Fill,
            )
        }
    }
}

private fun List<Int>.toDrawableAmplitudes(
    spikes: Int,
    minHeight: Float,
    maxHeight: Float
): List<Float> {
    val amplitudes = map(Int::toFloat)

    if (amplitudes.isEmpty() || spikes == 0)
        return List(spikes) { minHeight }

    val transform = { data: List<Float> ->
        data.average().toFloat().coerceIn(minHeight, maxHeight)
    }

    return when {
        spikes > amplitudes.size -> amplitudes.fillToSize(spikes, transform)
        else -> amplitudes.chunkToSize(spikes, transform)
    }.normalize(minHeight, maxHeight)
}

private fun <T> Iterable<T>.fillToSize(size: Int, transform: (List<T>) -> T): List<T> {
    val len = ceil(size safeDiv count()).roundToInt()
    return map { data -> List(len) { data } }.flatten().chunkToSize(size, transform)
}

private fun <T> Iterable<T>.chunkToSize(size: Int, transform: (List<T>) -> T): List<T> {
    val chunkSize = count() / size
    val remainder = count() % size
    val remainderIndex = ceil(count() safeDiv remainder).roundToInt()

    val chunkIteration = filterIndexed { index, _ ->
        remainderIndex == 0 || index % remainderIndex != 0
    }.chunked(chunkSize, transform)

    return when (size) {
        chunkIteration.size -> chunkIteration
        else -> chunkIteration.chunkToSize(size, transform)
    }
}

private fun Iterable<Float>.normalize(min: Float, max: Float) =
    map { (max - min) * ((it - min()) safeDiv (max() - min())) + min }

private infix fun Int.safeDiv(value: Int) = when (value) {
    0 -> 0F
    else -> this / value.toFloat()
}

private infix fun Long.safeDiv(value: Long) = when (value) {
    0L -> 0F
    else -> this / value.toFloat()
}

private infix fun Float.safeDiv(value: Float) = when (value) {
    0F -> 0F
    else -> this / value
}