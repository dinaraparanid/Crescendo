package com.paranid5.crescendo.trimmer.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.pitchAndSpeedFlow
import com.paranid5.crescendo.trimmer.presentation.properties.playbackAlphaFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
fun TrimmerViewModel.collectIsPlayerInitializedAsState() =
    isPlayerInitializedState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectIsPlayingAsState() =
    isPlayingState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectPitchAsState() =
    pitchState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectSpeedAsState() =
    speedState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectPlaybackAlphaAsState(initial: Float = 0F) =
    playbackAlphaFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectPitchAndSpeedAsState(initial: PitchAndSpeed = PitchAndSpeed()) =
    pitchAndSpeedFlow.collectLatestAsState(initial)