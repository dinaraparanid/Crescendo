package com.paranid5.crescendo.cache.presentation.properties

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.cache.data.cacheFormatFlow
import com.paranid5.crescendo.cache.data.isCacheButtonClickableFlow
import com.paranid5.crescendo.cache.data.trimRangeFlow
import com.paranid5.crescendo.cache.presentation.CacheViewModel
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun CacheViewModel.collectTrimOffsetMillisAsState() =
    trimOffsetMillisState.collectLatestAsState()

@Composable
internal fun CacheViewModel.collectTotalDurationMillisAsState() =
    totalDurationMillisState.collectLatestAsState()

@Composable
internal fun CacheViewModel.collectFilenameAsState() =
    filenameState.collectLatestAsState()

@Composable
internal fun CacheViewModel.collectSelectedSaveOptionIndexAsState() =
    selectedSaveOptionIndexState.collectLatestAsState()

@Composable
internal fun CacheViewModel.collectTrimRangeAsState(initial: TrimRange = TrimRange()) =
    trimRangeFlow.collectLatestAsState(initial)

@Composable
internal fun CacheViewModel.collectIsCacheButtonClickableAsState(initial: Boolean = false) =
    isCacheButtonClickableFlow.collectLatestAsState(initial)

@Composable
internal fun CacheViewModel.collectCacheFormatAsState(initial: Formats = Formats.MP3) =
    cacheFormatFlow.collectLatestAsState(initial)