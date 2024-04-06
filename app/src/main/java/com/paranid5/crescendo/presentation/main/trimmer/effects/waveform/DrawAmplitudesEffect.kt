package com.paranid5.crescendo.presentation.main.trimmer.effects.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.MIN_SPIKE_HEIGHT
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectAmplitudesAsState
import com.paranid5.crescendo.utils.extensions.safeDiv
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun DrawAmplitudesEffect(
    spikes: Float,
    canvasSize: Size,
    spikesAmplitudesState: MutableState<ImmutableList<Float>>,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val amplitudes by viewModel.collectAmplitudesAsState()
    var spikesAmplitudes by spikesAmplitudesState

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

private fun ImmutableList<Int>.toDrawableAmplitudes(
    spikes: Int,
    minHeight: Float,
    maxHeight: Float
): ImmutableList<Float> {
    val amplitudes = map(Int::toFloat)

    if (amplitudes.isEmpty() || spikes == 0)
        return List(spikes) { minHeight }.toImmutableList()

    val transform = { data: List<Float> ->
        data
            .average()
            .toFloat()
            .coerceIn(minHeight, maxOf(minHeight, maxHeight))
    }

    return when {
        spikes > amplitudes.size -> amplitudes.fillToSize(spikes, transform)
        else -> amplitudes.chunkToSize(spikes, transform)
    }.normalize(minHeight, maxHeight)
}

private fun <T> Iterable<T>.fillToSize(
    size: Int,
    transform: (List<T>) -> T
): ImmutableList<T> {
    val len = ceil(size safeDiv count()).roundToInt()

    return map { data -> List(len) { data } }
        .flatten()
        .chunkToSize(size, transform)
}

private fun <T> Iterable<T>.chunkToSize(
    size: Int,
    transform: (List<T>) -> T
): ImmutableList<T> {
    val chunkSize = count() / size
    val remainder = count() % size
    val remainderIndex = ceil(count() safeDiv remainder).roundToInt()

    val chunkIteration = filterIndexed { index, _ ->
        remainderIndex == 0 || index % remainderIndex != 0
    }.chunked(chunkSize, transform)

    return when (size) {
        chunkIteration.size -> chunkIteration.toImmutableList()
        else -> chunkIteration.chunkToSize(size, transform)
    }
}

private fun Iterable<Float>.normalize(min: Float, max: Float) =
    map { (max - min) * ((it - min()) safeDiv (max() - min())) + min }
        .toImmutableList()