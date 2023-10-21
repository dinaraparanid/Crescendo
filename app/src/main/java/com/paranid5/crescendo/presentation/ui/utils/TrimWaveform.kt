package com.paranid5.crescendo.presentation.ui.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.presentation.playing.PlayingPresenter
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.theme.TransparentUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.callback.AmplitudaErrorListener
import kotlin.math.ceil
import kotlin.math.roundToInt

private const val MinSpikeHeight = 1F
private const val DefaultGraphicsLayerAlpha = 0.99F

@Composable
fun TrimWaveform(
    url: String?,
    trimOffsetSecsState: MutableLongState,
    endPointSecsState: MutableLongState,
    lengthInSecs: Long,
    playingPresenter: PlayingPresenter,
    modifier: Modifier = Modifier,
    spikeAnimationSpec: AnimationSpec<Float> = tween(500),
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current.value
    val waveformBrush = SolidColor(TransparentUtility)
    val progressBrush = SolidColor(colors.primary)

    val amplituda by remember { derivedStateOf { Amplituda(context) } }
    val amplitudesState = playingPresenter.amplitudesState
    val amplitudes by amplitudesState.collectAsState()

    val trimOffsetSecs by trimOffsetSecsState
    val offset by remember { derivedStateOf { trimOffsetSecs safeDiv lengthInSecs } }

    val endPointSecs by endPointSecsState
    val endPoint by remember { derivedStateOf { endPointSecs safeDiv lengthInSecs } }

    var canvasSize by remember { mutableStateOf(Size(0F, 0F)) }
    var spikes by remember { mutableFloatStateOf(0F) }

    val spikesAmplitudes = remember(amplitudes, spikes) {
        amplitudes.toDrawableAmplitudes(
            spikes = spikes.toInt(),
            minHeight = MinSpikeHeight,
            maxHeight = canvasSize.height.coerceAtLeast(MinSpikeHeight)
        )
    }.map { animateFloatAsState(it, spikeAnimationSpec, label = "").value }

    LaunchedEffect(key1 = url) {
        withContext(Dispatchers.IO) {
            if (url != null) amplitudesState.update {
                amplituda
                    .processAudio(url)
                    .get(AmplitudaErrorListener { it.printStackTrace() })
                    .amplitudesAsList()
            }
        }
    }

    Canvas(
        modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = DefaultGraphicsLayerAlpha)
    ) {
        canvasSize = size
        spikes = size.width

        spikesAmplitudes.forEachIndexed { index, amplitude ->
            drawRoundRect(
                brush = waveformBrush,
                topLeft = Offset(
                    x = index.toFloat(),
                    y = size.height / 2F - amplitude / 2F
                ),
                size = Size(
                    width = 1F,
                    height = amplitude
                ),
                cornerRadius = CornerRadius(0F, 0F),
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