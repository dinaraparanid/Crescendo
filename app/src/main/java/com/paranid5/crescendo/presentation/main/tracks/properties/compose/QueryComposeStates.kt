package com.paranid5.crescendo.presentation.main.tracks.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
fun TracksViewModel.collectQueryAsState() =
    queryState.collectLatestAsState()