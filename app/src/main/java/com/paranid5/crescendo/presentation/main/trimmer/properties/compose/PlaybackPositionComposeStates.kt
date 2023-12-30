package com.paranid5.crescendo.presentation.main.trimmer.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.domain.trimming.FadeDurations
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.endOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.fadeDurationsFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackTextFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.startOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.trimRangeFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.trimmedDurationInMillisFlow
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState

@Composable
fun TrimmerViewModel.collectStartPosInMillisAsState() =
    startPosInMillisState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectEndPosInMillisAsState() =
    endPosInMillisState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectPlaybackPosInMillisAsState() =
    playbackPosInMillisState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectFadeInSecsAsState() =
    fadeInSecsState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectFadeOutAsState() =
    fadeOutSecsState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectStartOffsetAsState(initial: Float = 0F) =
    startOffsetFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectEndOffsetAsState(initial: Float = 0F) =
    endOffsetFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectTrimmedDurationInMillisAsState(initial: Long = 0L) =
    trimmedDurationInMillisFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectTrimRangeAsState(initial: TrimRange = TrimRange()) =
    trimRangeFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectFadeDurationsAsState(initial: FadeDurations = FadeDurations()) =
    fadeDurationsFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectPlaybackOffsetAsState(initial: Float = 0F) =
    playbackOffsetFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectPlaybackTextAsState(initial: String = "") =
    playbackTextFlow.collectLatestAsState(initial)