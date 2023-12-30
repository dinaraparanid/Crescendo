package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO

@Composable
fun Waveform(
    spikesAmplitudes: List<Float>,
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
