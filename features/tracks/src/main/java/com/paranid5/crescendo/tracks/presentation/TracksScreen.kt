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
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.tracks.presentation.effects.LoadTracksFromMediaStoreEffect
import com.paranid5.crescendo.tracks.presentation.properties.compose.collectSearchBarHeightDpAsState
import com.paranid5.crescendo.tracks.presentation.views.DefaultTrackList
import com.paranid5.crescendo.tracks.presentation.views.TrackSearcher
import com.paranid5.crescendo.tracks.presentation.views.TracksBar
import com.paranid5.crescendo.ui.appbar.appBarHeight
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
                    .height(searchBarHeight)
            ) { filtered, scrollingState ->
                viewModel.setFilteredTracks(filtered)
                tracksScrollingState = scrollingState
            }

            TracksBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(16.dp))

            DefaultTrackList(
                scrollingState = tracksScrollingState,
                bottomPadding = 8.dp,
                modifier = Modifier
                    .fillMaxSize(1F)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RefreshIndicator(
    refreshing: Boolean,
    refreshState: PullRefreshState,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    PullRefreshIndicator(
        refreshing = refreshing,
        state = refreshState,
        modifier = modifier,
        backgroundColor = colors.backgroundAlternative,
        contentColor = colors.primary
    )
}