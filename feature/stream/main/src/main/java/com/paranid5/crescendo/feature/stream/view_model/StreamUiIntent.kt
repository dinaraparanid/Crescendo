package com.paranid5.crescendo.feature.stream.view_model

import com.paranid5.crescendo.feature.stream.view_model.StreamState.PagerState

sealed interface StreamUiIntent {
    data class UpdatePagerState(val pagerState: PagerState) : StreamUiIntent
}
