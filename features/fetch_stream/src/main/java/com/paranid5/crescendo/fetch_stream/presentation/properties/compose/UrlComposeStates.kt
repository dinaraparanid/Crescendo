package com.paranid5.crescendo.fetch_stream.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.fetch_stream.presentation.FetchStreamViewModel
import com.paranid5.crescendo.fetch_stream.presentation.properties.currentTextFlow
import com.paranid5.crescendo.fetch_stream.presentation.properties.isConfirmButtonActiveFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun FetchStreamViewModel.collectCurrentTextAsState(initial: String = "") =
    currentTextFlow.collectLatestAsState(initial)

@Composable
internal fun FetchStreamViewModel.collectIsConfirmButtonActiveAsState(initial: Boolean = false) =
    isConfirmButtonActiveFlow.collectLatestAsState(initial)