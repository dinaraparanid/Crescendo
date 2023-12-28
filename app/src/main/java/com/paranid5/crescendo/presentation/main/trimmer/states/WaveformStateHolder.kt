package com.paranid5.crescendo.presentation.main.trimmer.states

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.amplitudesFlow
import com.paranid5.crescendo.data.properties.storeAmplitudes
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WaveformStateHolder(private val storageHandler: StorageHandler) :
    CoroutineScope by MainScope() {
    val amplitudesFlow by lazy { storageHandler.amplitudesFlow }

    fun setAmplitudesAsync(amplitudes: ImmutableList<Int>) = launch(Dispatchers.IO) {
        storageHandler.storeAmplitudes(amplitudes)
    }

    private val _zoomState by lazy { MutableStateFlow(0) }

    val zoomState by lazy { _zoomState.asStateFlow() }

    fun setZoom(zoomRatio: Int) = _zoomState.update { zoomRatio }

    private val _zoomStepsState by lazy { MutableStateFlow(0) }

    val zoomStepsState by lazy { _zoomStepsState.asStateFlow() }

    fun setZoomSteps(zoomSteps: Int) = _zoomStepsState.update { zoomSteps }
}

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