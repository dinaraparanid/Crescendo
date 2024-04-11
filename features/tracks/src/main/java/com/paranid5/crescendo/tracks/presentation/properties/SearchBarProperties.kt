package com.paranid5.crescendo.tracks.presentation.properties

import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.tracks.presentation.TracksViewModel
import kotlinx.coroutines.flow.map

internal inline val TracksViewModel.searchBarHeightDpFlow
    get() = isSearchBarActiveState.map { if (it) 80.dp else 60.dp }