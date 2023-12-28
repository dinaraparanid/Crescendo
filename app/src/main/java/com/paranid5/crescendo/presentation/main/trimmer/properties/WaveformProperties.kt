package com.paranid5.crescendo.presentation.main.trimmer.properties

import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.states.zoomIn
import com.paranid5.crescendo.presentation.main.trimmer.states.zoomOut
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

inline val TrimmerViewModel.amplitudesFlow
    get() = waveformStateHolder.amplitudesFlow

fun TrimmerViewModel.setAmplitudesAsync(amplitudes: ImmutableList<Int>) =
    waveformStateHolder.setAmplitudesAsync(amplitudes)

suspend inline fun TrimmerViewModel.setAmplitudes(amplitudes: ImmutableList<Int>) =
    waveformStateHolder.setAmplitudesAsync(amplitudes).join()

inline val TrimmerViewModel.zoomState
    get() = waveformStateHolder.zoomState

fun TrimmerViewModel.setZoom(zoomRatio: Int) =
    waveformStateHolder.setZoom(zoomRatio)

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

fun TrimmerViewModel.zoomIn() =
    waveformStateHolder.zoomIn()

inline val TrimmerViewModel.canZoomOutFlow
    get() = zoomState.map { it > 0 }

fun TrimmerViewModel.zoomOut() =
    waveformStateHolder.zoomOut()

inline val TrimmerViewModel.zoomStepsState
    get() = waveformStateHolder.zoomStepsState

fun TrimmerViewModel.setZoomSteps(zoomSteps: Int) =
    waveformStateHolder.setZoomSteps(zoomSteps)