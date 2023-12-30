package com.paranid5.crescendo.presentation.main.tracks.properties.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.tracks.properties.searchBarHeightDpFlow
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState

@Composable
fun TracksViewModel.collectIsSearchBarActiveAsState() =
    isSearchBarActiveState.collectLatestAsState()

@Composable
fun TracksViewModel.collectSearchBarHeightDpAsState() =
    searchBarHeightDpFlow.collectLatestAsState(initial = 1.dp)