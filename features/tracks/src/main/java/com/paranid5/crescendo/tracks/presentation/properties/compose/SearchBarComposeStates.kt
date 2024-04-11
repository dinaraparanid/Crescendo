package com.paranid5.crescendo.tracks.presentation.properties.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.tracks.presentation.TracksViewModel
import com.paranid5.crescendo.tracks.presentation.properties.searchBarHeightDpFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun TracksViewModel.collectIsSearchBarActiveAsState() =
    isSearchBarActiveState.collectLatestAsState()

@Composable
internal fun TracksViewModel.collectSearchBarHeightDpAsState() =
    searchBarHeightDpFlow.collectLatestAsState(initial = 1.dp)