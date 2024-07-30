package com.paranid5.crescendo.tracks.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.tracks.presentation.effects.LoadTracksFromMediaStoreEffect
import com.paranid5.crescendo.tracks.presentation.properties.compose.collectSearchBarHeightDpAsState
import com.paranid5.crescendo.tracks.presentation.views.DefaultTrackList
import com.paranid5.crescendo.tracks.presentation.views.TrackSearcher
import com.paranid5.crescendo.tracks.presentation.views.TracksBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TracksScreen(
    modifier: Modifier = Modifier,
    viewModel: TracksViewModel = koinViewModel(),
) {
    val searchBarHeight by viewModel.collectSearchBarHeightDpAsState()
    var tracksScrollingState = rememberLazyListState()

    val refreshingState = remember { mutableStateOf(true) }
    var refreshing by refreshingState

    val refreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { refreshing = true }
    )

    LoadTracksFromMediaStoreEffect(refreshingState)

    Box(modifier.pullRefresh(refreshState)) {
        RefreshIndicator(
            refreshing = refreshing,
            refreshState = refreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Column(Modifier.fillMaxSize()) {
            TrackSearcher(
                Modifier
                    .fillMaxWidth()
                    .height(searchBarHeight),
            ) { filtered, scrollingState ->
                viewModel.setFilteredTracks(filtered)
                tracksScrollingState = scrollingState
            }

            TracksBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.padding.small),
            )

            Spacer(Modifier.height(dimensions.padding.extraMedium))

            DefaultTrackList(
                scrollingState = tracksScrollingState,
                bottomPadding = dimensions.padding.small,
                modifier = Modifier
                    .fillMaxSize(1F)
                    .padding(horizontal = dimensions.padding.small),
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RefreshIndicator(
    refreshing: Boolean,
    refreshState: PullRefreshState,
    modifier: Modifier = Modifier,
) = PullRefreshIndicator(
    refreshing = refreshing,
    state = refreshState,
    modifier = modifier,
    backgroundColor = colors.background.alternative,
    contentColor = colors.primary,
)