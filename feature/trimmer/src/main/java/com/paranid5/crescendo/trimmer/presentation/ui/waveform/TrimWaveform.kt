package com.paranid5.crescendo.trimmer.presentation.ui.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.effects.waveform.DrawAmplitudesEffect
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun TrimWaveform(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    val spikesAmplitudesState: MutableState<ImmutableList<Float>> = remember {
        mutableStateOf(persistentListOf())
    }

    val spikesAmplitudes by spikesAmplitudesState

    val spikesState = remember {
        mutableFloatStateOf(1F)
    }

    val spikes by spikesState

    val canvasSizeState = remember {
        mutableStateOf(Size(1F, 1F))
    }

    val canvasSize by canvasSizeState

    DrawAmplitudesEffect(
        state = state,
        spikes = spikes,
        canvasSize = canvasSize,
        spikesAmplitudesState = spikesAmplitudesState,
    )

    TrimmerWaveformContent(
        state = state,
        onUiIntent = onUiIntent,
        canvasSizeState = canvasSizeState,
        spikesState = spikesState,
        spikesAmplitudes = spikesAmplitudes,
        modifier = modifier,
        spikeWidthRatio = spikeWidthRatio,
    )
}