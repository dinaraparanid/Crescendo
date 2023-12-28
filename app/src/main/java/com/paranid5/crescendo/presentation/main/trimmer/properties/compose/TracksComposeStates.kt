package com.paranid5.crescendo.presentation.main.trimmer.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.trackDurationInMillisFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.trackOrNullState
import com.paranid5.crescendo.presentation.main.trimmer.properties.trackPathOrNullFlow
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState

@Composable
fun TrimmerViewModel.collectTrackAsState() =
    trackOrNullState.collectLatestAsState()

@Composable
fun TrimmerViewModel.collectTrackDurationInMillisAsState(initial: Long = 0) =
    trackDurationInMillisFlow.collectLatestAsState(initial)

@Composable
fun TrimmerViewModel.collectTrackPathAsState(initial: String? = null) =
    trackPathOrNullFlow.collectLatestAsState(initial)