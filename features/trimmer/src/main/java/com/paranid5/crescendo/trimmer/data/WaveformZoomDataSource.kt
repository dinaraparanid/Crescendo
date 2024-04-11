package com.paranid5.crescendo.trimmer.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal interface WaveformZoomDataSource {
    val zoomState: StateFlow<Int>
    val zoomStepsState: StateFlow<Int>

    fun setZoom(zoomRatio: Int)
    fun setZoomSteps(zoomSteps: Int)
}

internal class WaveformZoomDataSourceImpl : WaveformZoomDataSource, CoroutineScope by MainScope() {
    private val _zoomState by lazy {
        MutableStateFlow(0)
    }

    override val zoomState by lazy {
        _zoomState.asStateFlow()
    }

    override fun setZoom(zoomRatio: Int) =
        _zoomState.update { zoomRatio }

    private val _zoomStepsState by lazy {
        MutableStateFlow(0)
    }

    override val zoomStepsState by lazy {
        _zoomStepsState.asStateFlow()
    }

    override fun setZoomSteps(zoomSteps: Int) =
        _zoomStepsState.update { zoomSteps }
}