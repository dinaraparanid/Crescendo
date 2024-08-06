package com.paranid5.crescendo.feature.play.main.presentation.view_model

import com.paranid5.crescendo.feature.play.main.presentation.view_model.PlayState.PagerState

interface PlayUiIntent {
    data class UpdateSearchQuery(val query: String) : PlayUiIntent

    data object SearchCancelClick : PlayUiIntent

    data class UpdatePagerState(val pagerState: PagerState) : PlayUiIntent

    data class ShowTrimmer(val trackUri: String) : PlayUiIntent
}
