package com.paranid5.crescendo.trimmer.presentation.effects.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.utils.extensions.pxToDp

@Composable
internal fun InitZoomStepsEffect(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    screenWidthPx: Int,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    val viewport = screenWidthPx.pxToDp().value.toInt()

    val waveformWidth by remember(state) {
        derivedStateOf { state.waveformMaxWidth(spikeWidthRatio = spikeWidthRatio) }
    }

    LaunchedEffect(viewport, waveformWidth) {
        val steps = zoomSteps(waveformWidth = waveformWidth, viewport = viewport)
        onUiIntent(TrimmerUiIntent.Waveform.UpdateZoomSteps(zoomSteps = steps))
        onUiIntent(TrimmerUiIntent.Waveform.UpdateZoomLevel(zoom = 0))
    }
}

private fun zoomSteps(waveformWidth: Int, viewport: Int): Int {
    tailrec fun impl(width: Int = waveformWidth, cnt: Int = 0): Int = when {
        width > viewport -> impl(width = width / 2, cnt = cnt + 1)
        else -> cnt
    }

    return impl()
}
