package com.paranid5.crescendo.presentation.main.playing.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.states.cacheFormatFlow
import com.paranid5.crescendo.presentation.main.playing.states.isCacheButtonClickableFlow
import com.paranid5.crescendo.presentation.main.playing.states.trimRangeFlow
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState

@Composable
fun PlayingViewModel.collectTrimOffsetMillisAsState() =
    trimOffsetMillisState.collectLatestAsState()

@Composable
fun PlayingViewModel.collectTotalDurationMillisAsState() =
    totalDurationMillisState.collectLatestAsState()

@Composable
fun PlayingViewModel.collectFilenameAsState() =
    filenameState.collectLatestAsState()

@Composable
fun PlayingViewModel.collectSelectedSaveOptionIndexAsState() =
    selectedSaveOptionIndexState.collectLatestAsState()

@Composable
fun PlayingViewModel.collectTrimRangeAsState(initial: TrimRange = TrimRange()) =
    trimRangeFlow.collectLatestAsState(initial)

@Composable
fun PlayingViewModel.collectIsCacheButtonClickableAsState(initial: Boolean = false) =
    isCacheButtonClickableFlow.collectLatestAsState(initial)

@Composable
fun PlayingViewModel.collectCacheFormatAsState(initial: Formats = Formats.MP3) =
    cacheFormatFlow.collectLatestAsState(initial)