package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel

@Composable
fun Waveform(
    viewModel: TrimmerViewModel,
    canvasSizeState: MutableState<Size>,
    spikesState: MutableFloatState,
    spikesAmplitudesState: State<List<Float>>,
    modifier: Modifier = Modifier
) {
    WaveformSpikes(
        canvasSizeState = canvasSizeState,
        spikesState = spikesState,
        spikesAmplitudesState = spikesAmplitudesState,
        viewModel = viewModel,
        modifier = modifier
    )

    TrimmedZone(viewModel = viewModel, modifier = modifier)
}
