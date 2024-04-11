package com.paranid5.crescendo.trimmer.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.canZoomInFlow
import com.paranid5.crescendo.trimmer.presentation.properties.canZoomOutFlow
import com.paranid5.crescendo.trimmer.presentation.properties.waveformMaxWidthFlow
import com.paranid5.crescendo.trimmer.presentation.properties.waveformWidthFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
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