package com.paranid5.crescendo.presentation.main.trimmer.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.pitchAndSpeedFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackAlphaFlow
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
fun TrimmerViewModel.collectPitchAndSpeedAsState(initial: com.paranid5.crescendo.core.common.trimming.PitchAndSpeed = com.paranid5.crescendo.core.common.trimming.PitchAndSpeed()) =
    pitchAndSpeedFlow.collectLatestAsState(initial)