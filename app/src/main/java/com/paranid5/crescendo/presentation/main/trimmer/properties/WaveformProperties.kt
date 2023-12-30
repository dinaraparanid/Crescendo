package com.paranid5.crescendo.presentation.main.trimmer.properties

import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.states.WaveformStateHolder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun WaveformStateHolder.zoomIn() =
    setZoom(
        when (val zoom = zoom) {
            zoomSteps -> zoom
            else -> zoom + 1
        }
    )

fun WaveformStateHolder.zoomOut() =
    setZoom(
        when (val zoom = zoom) {
            0 -> 0
            else -> zoom - 1
        }
    )

private inline val WaveformStateHolder.zoom
    get() = zoomState.value

private inline val WaveformStateHolder.zoomSteps
    get() = zoomStepsState.value

fun TrimmerViewModel.waveformWidthFlow(spikeWidthRatio: Int) =
    combine(
        trackDurationInMillisFlow,
        zoomState,
        zoomStepsState
    ) { durationMillis, zoom, zoomSteps ->
        (durationMillis / 1000 * spikeWidthRatio / (1 shl (zoomSteps - zoom))).toInt()
    }

fun TrimmerViewModel.waveformMaxWidthFlow(spikeWidthRatio: Int) =
    trackDurationInMillisFlow.map { (it / 1000 * spikeWidthRatio).toInt() }


inline val TrimmerViewModel.canZoomInFlow
    get() = combine(
        zoomState,
        zoomStepsState
    ) { zoom, zoomSteps ->
        zoom < zoomSteps
    }

inline val TrimmerViewModel.canZoomOutFlow
    get() = zoomState.map { it > 0 }

fun TrimmerViewModel.setAmplitudesAsync(amplitudes: ImmutableList<Int>) =
    viewModelScope.launch(Dispatchers.IO) { setAmplitudes(amplitudes) }