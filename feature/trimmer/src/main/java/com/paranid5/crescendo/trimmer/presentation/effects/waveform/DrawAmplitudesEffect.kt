package com.paranid5.crescendo.trimmer.presentation.effects.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.trimmer.presentation.MIN_SPIKE_HEIGHT
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.utils.extensions.ImmutableList
import com.paranid5.crescendo.utils.extensions.mapToImmutableList
import com.paranid5.crescendo.utils.extensions.safeDiv
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
internal fun DrawAmplitudesEffect(
    state: TrimmerState,
    spikes: Float,
    canvasSize: Size,
    spikesAmplitudesState: MutableState<ImmutableList<Float>>,
) {
    val amplitudes = remember(state.amplitudes) { state.amplitudes }
    var spikesAmplitudes by spikesAmplitudesState

    LaunchedEffect(amplitudes, spikes, canvasSize) {
        spikesAmplitudes = withContext(Dispatchers.IO) {
            amplitudes.toDrawableAmplitudes(
                spikes = spikes.toInt(),
                minHeight = MIN_SPIKE_HEIGHT,
                maxHeight = canvasSize.height
                    .coerceAtLeast(MIN_SPIKE_HEIGHT) - CONTROLLER_HEIGHT_OFFSET,
            )
        }
    }
}

private fun List<Int>.toDrawableAmplitudes(
    spikes: Int,
    minHeight: Float,
    maxHeight: Float,
): ImmutableList<Float> {
    val amplitudes = map(Int::toFloat)

    if (amplitudes.isEmpty() || spikes == 0)
        return ImmutableList(size = spikes, elem = minHeight)

    fun transform(data: List<Float>) =
        data
            .average()
            .toFloat()
            .coerceIn(minHeight, maxOf(minHeight, maxHeight))

    return when {
        spikes > amplitudes.size -> amplitudes.fillToSize(size = spikes, transform = ::transform)
        else -> amplitudes.chunkToSize(size = spikes, transform = ::transform)
    }.normalize(min = minHeight, max = maxHeight)
}

private fun <T> Iterable<T>.fillToSize(
    size: Int,
    transform: (List<T>) -> T
): List<T> {
    val len = ceil(size safeDiv count()).roundToInt()

    return map { data -> List(len) { data } }
        .flatten()
        .chunkToSize(size, transform)
}

private fun <T> Iterable<T>.chunkToSize(
    size: Int,
    transform: (List<T>) -> T
): List<T> {
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
    mapToImmutableList { (max - min) * ((it - min()) safeDiv (max() - min())) + min }
