package com.paranid5.crescendo.presentation.main.trimmer.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.amplitudesFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.canZoomInFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.canZoomOutFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.waveformMaxWidthFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.waveformWidthFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.zoomState
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun TrimmerViewModel.collectAmplitudesAsState(initial: ImmutableList<Int> = persistentListOf()) =
    amplitudesFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectZoomAsState() =
    zoomState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectWaveformWidthAsState(spikeWidthRatio: Int, initial: Int = 0) =
    waveformWidthFlow(spikeWidthRatio).collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectWaveformMaxWidthAsState(spikeWidthRatio: Int, initial: Int = 0) =
    waveformMaxWidthFlow(spikeWidthRatio).collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectCanZoomInAsState(initial: Boolean = false) =
    canZoomInFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectCanZoomOutAsState(initial: Boolean = false) =
    canZoomOutFlow.collectLatestAsState(initial)