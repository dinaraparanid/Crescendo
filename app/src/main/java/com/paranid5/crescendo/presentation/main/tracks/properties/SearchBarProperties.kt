package com.paranid5.crescendo.presentation.main.tracks.properties

import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import kotlinx.coroutines.flow.map

inline val TracksViewModel.isSearchBarActiveState
    get() = searchBarStateHolder.isSearchBarActiveState

fun TracksViewModel.setSearchBarActive(isActive: Boolean) =
    searchBarStateHolder.setSearchBarActive(isActive)

inline val TracksViewModel.searchBarHeightDpFlow
    get() = isSearchBarActiveState.map { if (it) 80.dp else 60.dp }