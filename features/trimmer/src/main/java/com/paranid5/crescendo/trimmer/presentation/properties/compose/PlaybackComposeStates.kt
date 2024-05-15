package com.paranid5.crescendo.trimmer.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.pitchAndSpeedFlow
import com.paranid5.crescendo.trimmer.presentation.properties.playbackAlphaFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun TrimmerViewModel.collectIsPlayerInitializedAsState() =
    isPlayerInitializedState.collectLatestAsState()

@Composable
internal fun TrimmerViewModel.collectIsPlayingAsState() =
    isPlayingState.collectLatestAsState()

@Composable
internal fun TrimmerViewModel.collectPitchAsState() =
    pitchState.collectLatestAsState()

@Composable
internal fun TrimmerViewModel.collectSpeedAsState() =
    speedState.collectLatestAsState()

@Composable
internal fun TrimmerViewModel.collectPlaybackAlphaAsState(initial: Float = 0F) =
    playbackAlphaFlow.collectLatestAsState(initial)

@Composable
internal fun TrimmerViewModel.collectPitchAndSpeedAsState(initial: PitchAndSpeed = PitchAndSpeed()) =
    pitchAndSpeedFlow.collectLatestAsState(initial)

@Composable
internal fun TrimmerViewModel.collectFocusEventAsState() =
    focusEventState.collectLatestAsState()