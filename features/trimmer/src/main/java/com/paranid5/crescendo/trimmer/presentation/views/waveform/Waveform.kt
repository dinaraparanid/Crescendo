package com.paranid5.crescendo.trimmer.presentation.views.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun Waveform(
    spikesAmplitudes: ImmutableList<Float>,
    canvasSizeState: MutableState<Size>,
    spikesState: MutableFloatState,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO
) {
    WaveformSpikes(
        canvasSizeState = canvasSizeState,
        spikesState = spikesState,
        spikesAmplitudes = spikesAmplitudes,
        spikeWidthRatio = spikeWidthRatio,
        modifier = modifier
    )

    TrimmedZone(modifier)
}
