package com.paranid5.crescendo.playing.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.playing.data.cacheFormatFlow
import com.paranid5.crescendo.playing.data.isCacheButtonClickableFlow
import com.paranid5.crescendo.playing.data.trimRangeFlow
import com.paranid5.crescendo.playing.presentation.PlayingViewModel
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun PlayingViewModel.collectTrimOffsetMillisAsState() =
    trimOffsetMillisState.collectLatestAsState()

@Composable
internal fun PlayingViewModel.collectTotalDurationMillisAsState() =
    totalDurationMillisState.collectLatestAsState()

@Composable
internal fun PlayingViewModel.collectFilenameAsState() =
    filenameState.collectLatestAsState()

@Composable
internal fun PlayingViewModel.collectSelectedSaveOptionIndexAsState() =
    selectedSaveOptionIndexState.collectLatestAsState()

@Composable
internal fun PlayingViewModel.collectTrimRangeAsState(initial: TrimRange = TrimRange()) =
    trimRangeFlow.collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectIsCacheButtonClickableAsState(initial: Boolean = false) =
    isCacheButtonClickableFlow.collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectCacheFormatAsState(initial: Formats = Formats.MP3) =
    cacheFormatFlow.collectLatestAsState(initial)