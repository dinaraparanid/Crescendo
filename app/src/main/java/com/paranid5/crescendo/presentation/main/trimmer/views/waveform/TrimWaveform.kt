package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.presentation.main.trimmer.effects.waveform.DrawAmplitudesEffect
import com.paranid5.crescendo.presentation.main.trimmer.effects.waveform.LoadAmplitudesEffect

@Composable
fun TrimWaveform(
    viewModel: TrimmerViewModel,
    waveformScrollState: ScrollState,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    val spikesAmplitudesState = remember { mutableStateOf(listOf<Float>()) }
    val spikesState = remember { mutableFloatStateOf(1F) }
    val canvasSizeState = remember { mutableStateOf(Size(1F, 1F)) }

    LoadAmplitudesEffect(viewModel)

    DrawAmplitudesEffect(
        spikesAmplitudesState = spikesAmplitudesState,
        spikesState = spikesState,
        canvasSizeState = canvasSizeState,
        viewModel = viewModel
    )

    TrimmerWaveformContent(
        viewModel = viewModel,
        waveformScrollState = waveformScrollState,
        canvasSizeState = canvasSizeState,
        spikesState = spikesState,
        spikesAmplitudesState = spikesAmplitudesState,
        modifier = modifier,
        spikeWidthRatio = spikeWidthRatio
    )
}