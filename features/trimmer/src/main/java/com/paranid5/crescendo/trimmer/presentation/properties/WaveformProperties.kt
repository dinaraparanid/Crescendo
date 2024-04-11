package com.paranid5.crescendo.trimmer.presentation.properties

import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.data.WaveformZoomDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal fun WaveformZoomDataSource.zoomIn() =
    setZoom(
        when (val zoom = zoom) {
            zoomSteps -> zoom
            else -> zoom + 1
        }
    )

internal fun WaveformZoomDataSource.zoomOut() =
    setZoom(
        when (val zoom = zoom) {
            0 -> 0
            else -> zoom - 1
        }
    )

private inline val WaveformZoomDataSource.zoom
    get() = zoomState.value

private inline val WaveformZoomDataSource.zoomSteps
    get() = zoomStepsState.value

internal fun TrimmerViewModel.waveformWidthFlow(spikeWidthRatio: Int) =
    combine(
        trackDurationInMillisFlow,
        zoomState,
        zoomStepsState
    ) { durationMillis, zoom, zoomSteps ->
        (durationMillis / 1000 * spikeWidthRatio / (1 shl (zoomSteps - zoom))).toInt()
    }

internal fun TrimmerViewModel.waveformMaxWidthFlow(spikeWidthRatio: Int) =
    trackDurationInMillisFlow.map { (it / 1000 * spikeWidthRatio).toInt() }


internal inline val TrimmerViewModel.canZoomInFlow
    get() = combine(
        zoomState,
        zoomStepsState
    ) { zoom, zoomSteps ->
        zoom < zoomSteps
    }

internal inline val TrimmerViewModel.canZoomOutFlow
    get() = zoomState.map { it > 0 }

internal fun TrimmerViewModel.setAmplitudesAsync(amplitudes: ImmutableList<Int>) =
    viewModelScope.launch(Dispatchers.IO) { setAmplitudes(amplitudes) }