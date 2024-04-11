package com.paranid5.crescendo.trimmer.presentation.effects.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectWaveformMaxWidthAsState
import com.paranid5.crescendo.utils.extensions.pxToDp
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun InitZoomStepsEffect(
    screenWidthPxState: MutableIntState,
    viewModel: TrimmerViewModel = koinViewModel(),
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO
) {
    val screenWidthPx by screenWidthPxState
    val viewport = screenWidthPx.pxToDp().value.toInt()

    val waveformWidth by viewModel.collectWaveformMaxWidthAsState(spikeWidthRatio)

    LaunchedEffect(viewport) {
        val steps = zoomSteps(waveformWidth = waveformWidth, viewport = viewport)
        viewModel.setZoomSteps(steps)
        viewModel.setZoom(0)
    }
}

private fun zoomSteps(waveformWidth: Int, viewport: Int): Int {
    var width = waveformWidth
    var cnt = 0

    while (width > viewport) {
        width /= 2
        ++cnt
    }

    return cnt
}