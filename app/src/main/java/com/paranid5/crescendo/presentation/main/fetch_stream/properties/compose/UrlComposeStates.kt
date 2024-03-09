package com.paranid5.crescendo.presentation.main.fetch_stream.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.presentation.main.fetch_stream.FetchStreamViewModel
import com.paranid5.crescendo.presentation.main.fetch_stream.properties.currentTextFlow
import com.paranid5.crescendo.presentation.main.fetch_stream.properties.isConfirmButtonActiveFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
fun FetchStreamViewModel.collectCurrentTextAsState(initial: String = "") =
    currentTextFlow.collectLatestAsState(initial)

@Composable
fun FetchStreamViewModel.collectIsConfirmButtonActiveAsState(initial: Boolean = false) =
    isConfirmButtonActiveFlow.collectLatestAsState(initial)