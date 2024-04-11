package com.paranid5.crescendo.tracks.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.tracks.presentation.TracksViewModel
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun TracksViewModel.collectQueryAsState() =
    queryState.collectLatestAsState()