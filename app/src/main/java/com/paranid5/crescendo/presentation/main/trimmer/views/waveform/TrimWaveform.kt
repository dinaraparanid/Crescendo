package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_PADDING
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.presentation.main.trimmer.effects.waveform.DrawAmplitudesEffect
import com.paranid5.crescendo.presentation.main.trimmer.effects.waveform.LoadAmplitudesEffect
import com.paranid5.crescendo.presentation.main.trimmer.properties.endOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackAlphaFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.startOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.waveformWidthFlow
import com.paranid5.crescendo.presentation.ui.extensions.pxToDp

@Composable
fun TrimWaveform(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    val startOffset by viewModel.startOffsetFlow.collectAsState(initial = 0F)
    val endOffset by viewModel.endOffsetFlow.collectAsState(initial = 0F)
    val playbackOffset by viewModel.playbackOffsetFlow.collectAsState(initial = 0F)

    val playbackAlpha by viewModel.playbackAlphaFlow.collectAsState(initial = 0F)
    val playbackAlphaAnim by animateFloatAsState(playbackAlpha, label = "")

    val spikesAmplitudesState = remember { mutableStateOf(listOf<Float>()) }
    val spikesState = remember { mutableFloatStateOf(1F) }

    val canvasSizeState = remember { mutableStateOf(Size(1F, 1F)) }

    val waveformWidth by viewModel
        .waveformWidthFlow(spikeWidthRatio)
        .collectAsState(initial = 0)

    LoadAmplitudesEffect(viewModel)

    DrawAmplitudesEffect(
        spikesAmplitudesState = spikesAmplitudesState,
        spikesState = spikesState,
        canvasSizeState = canvasSizeState,
        viewModel = viewModel
    )

    Box(modifier) {
        Waveform(
            viewModel = viewModel,
            canvasSizeState = canvasSizeState,
            spikesState = spikesState,
            spikesAmplitudesState = spikesAmplitudesState,
            modifier = Modifier
                .width(waveformWidth.dp)
                .padding(horizontal = WAVEFORM_PADDING.toInt().pxToDp())
                .fillMaxHeight()
        )

        StartBorder(
            viewModel = viewModel,
            spikeWidthRatio = spikeWidthRatio,
            modifier = Modifier
                .offset(x = StartBorderOffset(startOffset, waveformWidth).dp)
                .fillMaxHeight()
                .zIndex(10F)
        )

        EndBorder(
            viewModel = viewModel,
            spikeWidthRatio = spikeWidthRatio,
            modifier = Modifier
                .offset(x = EndBorderOffset(endOffset, waveformWidth).dp)
                .fillMaxHeight()
                .zIndex(10F)
        )

        PlaybackPosition(
            modifier = Modifier
                .offset(x = PlaybackPositionOffset(playbackOffset, waveformWidth).dp)
                .alpha(playbackAlphaAnim)
                .fillMaxHeight()
                .zIndex(8F)
        )
    }
}