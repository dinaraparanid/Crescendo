package com.paranid5.crescendo.presentation.main.trimmer

import androidx.compose.runtime.Stable
import androidx.compose.ui.focus.FocusRequester

@Stable
data class FocusPoints(
    val startBorderFocusRequester: FocusRequester,
    val playbackFocusRequester: FocusRequester,
    val endBorderFocusRequester: FocusRequester
)
