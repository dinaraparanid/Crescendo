package com.paranid5.crescendo.presentation.main.tracks.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.tracks.properties.queryState
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState

@Composable
fun TracksViewModel.collectQueryAsState() =
    queryState.collectLatestAsState()