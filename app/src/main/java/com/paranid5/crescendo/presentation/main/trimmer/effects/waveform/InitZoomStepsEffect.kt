package com.paranid5.crescendo.presentation.main.trimmer.effects.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.setZoom
import com.paranid5.crescendo.presentation.main.trimmer.properties.setZoomSteps
import com.paranid5.crescendo.presentation.main.trimmer.properties.waveformMaxWidthFlow
import com.paranid5.crescendo.presentation.ui.extensions.pxToDp

@Composable
fun InitZoomStepsEffect(
    viewModel: TrimmerViewModel,
    screenWidthPxState: MutableIntState,
    spikeWidthRatio: Int
) {
    val screenWidthPx by screenWidthPxState
    val viewport = screenWidthPx.pxToDp().value.toInt()

    val waveformWidth by viewModel
        .waveformMaxWidthFlow(spikeWidthRatio)
        .collectAsState(initial = 0)

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