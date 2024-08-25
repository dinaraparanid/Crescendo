package com.paranid5.crescendo.trimmer.domain.entities

import androidx.compose.runtime.Stable
import androidx.compose.ui.focus.FocusRequester

@Stable
internal data class FocusPoints(
    val startBorderFocusRequester: FocusRequester,
    val playbackFocusRequester: FocusRequester,
    val endBorderFocusRequester: FocusRequester
)
