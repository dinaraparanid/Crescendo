package com.paranid5.crescendo.presentation.main.trimmer.effects.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.MIN_SPIKE_HEIGHT
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.amplitudesFlow
import com.paranid5.crescendo.presentation.ui.extensions.safeDiv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun DrawAmplitudesEffect(
    spikesAmplitudesState: MutableState<List<Float>>,
    spikesState: FloatState,
    canvasSizeState: State<Size>,
    viewModel: TrimmerViewModel,
) {
    val amplitudes by viewModel
        .amplitudesFlow
        .collectAsState(initial = emptyList())

    var spikesAmplitudes by spikesAmplitudesState
    val spikes by spikesState
    val canvasSize by canvasSizeState

    LaunchedEffect(amplitudes, spikes, canvasSize) {
        spikesAmplitudes = withContext(Dispatchers.IO) {
            amplitudes.toDrawableAmplitudes(
                spikes = spikes.toInt(),
                minHeight = MIN_SPIKE_HEIGHT,
                maxHeight = canvasSize.height
                    .coerceAtLeast(MIN_SPIKE_HEIGHT) - CONTROLLER_HEIGHT_OFFSET
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
        data.average().toFloat().coerceIn(minHeight, maxOf(minHeight, maxHeight))
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