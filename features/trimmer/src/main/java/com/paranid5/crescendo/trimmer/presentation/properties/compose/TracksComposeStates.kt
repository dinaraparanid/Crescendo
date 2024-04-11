package com.paranid5.crescendo.trimmer.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.trackDurationInMillisFlow
import com.paranid5.crescendo.trimmer.presentation.properties.trackPathOrNullFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
fun TrimmerViewModel.collectTrackAsState() =
    trackState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectTrackDurationInMillisAsState(initial: Long = 0) =
    trackDurationInMillisFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectTrackPathAsState(initial: String? = null) =
    trackPathOrNullFlow.collectLatestAsState(initial)