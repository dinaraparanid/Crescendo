package com.paranid5.crescendo.tracks.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.tracks.presentation.effect.LifecycleEffect
import com.paranid5.crescendo.tracks.presentation.effect.QueryUpdatesEffect
import com.paranid5.crescendo.tracks.presentation.effect.ScreenEffect
import com.paranid5.crescendo.tracks.presentation.ui.DefaultTrackList
import com.paranid5.crescendo.tracks.presentation.ui.TracksBar
import com.paranid5.crescendo.tracks.view_model.TracksScreenEffect
import com.paranid5.crescendo.tracks.view_model.TracksUiIntent
import com.paranid5.crescendo.tracks.view_model.TracksViewModel
import com.paranid5.crescendo.tracks.view_model.TracksViewModelImpl
import com.paranid5.crescendo.ui.foundation.AppRefreshIndicator
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.rememberPullRefreshWithDuration
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TracksScreen(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    viewModel: TracksViewModel = koinViewModel<TracksViewModelImpl>(),
    onScreenEffect: (TracksScreenEffect) -> Unit,
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    val (refreshState, isRefreshing) = rememberPullRefreshWithDuration(
        isRefreshing = state.shownTracksState is UiState.Refreshing,
        onRefresh = { onUiIntent(TracksUiIntent.Lifecycle.OnRefresh) },
    )

    LifecycleEffect(onUiIntent = onUiIntent)

    ScreenEffect(state = state, onScreenEffect = onScreenEffect) {
        onUiIntent(TracksUiIntent.ScreenEffect.ClearBackResult)
    }

    QueryUpdatesEffect(searchQuery, onUiIntent = onUiIntent)

    Box(modifier.pullRefresh(refreshState)) {
        Column(Modifier.fillMaxSize()) {
            TracksBar(
                state = state,
                onUiIntent = onUiIntent,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(dimensions.padding.extraMedium))

            DefaultTrackList(
                state = state,
                onUiIntent = onUiIntent,
                bottomPadding = dimensions.padding.small,
                modifier = Modifier.fillMaxSize(1F),
            )
        }

        AppRefreshIndicator(
            refreshing = isRefreshing,
            refreshState = refreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}
