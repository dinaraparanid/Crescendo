package com.paranid5.crescendo.feature.stream.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.feature.stream.fetch.presentation.FetchStreamScreen
import com.paranid5.crescendo.feature.stream.view_model.StreamState
import com.paranid5.crescendo.feature.stream.view_model.StreamState.PagerState
import com.paranid5.crescendo.feature.stream.view_model.StreamUiIntent
import com.paranid5.crescendo.ui.pager.AppPager
import com.paranid5.crescendo.ui.pager.PagerUiState
import com.paranid5.crescendo.utils.doNothing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun StreamPager(
    state: StreamState,
    onUiIntent: (StreamUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activePageIndex by rememberActivePageIndex(state = state)

    AppPager(
        pagerUiStates = rememberStreamPagerUiStates(),
        activePage = activePageIndex,
        modifier = modifier,
        onActivePageChanged = { pageIndex ->
            onUiIntent(StreamUiIntent.UpdatePagerState(PagerState.entries[pageIndex]))
        },
    ) { page ->
        val pageModifier = Modifier.fillMaxSize()

        when (PagerState.entries.getOrNull(page)) {
            PagerState.FETCH -> FetchStreamScreen(modifier = pageModifier)

            PagerState.RECENT -> Box(pageModifier) {
                Text("TODO: Recent Screen", Modifier.align(Alignment.Center))
            }

            null -> doNothing
        }
    }
}

@Composable
private fun rememberStreamPagerUiStates(): ImmutableList<PagerUiState> {
    val fetch = stringResource(R.string.stream_fetch_tab)
    val recent = stringResource(R.string.stream_recent_tab)
    return remember(fetch, recent) {
        persistentListOf(
            PagerUiState(title = fetch),
            PagerUiState(title = recent),
        )
    }
}

@Composable
private fun rememberActivePageIndex(state: StreamState) =
    remember(state) { derivedStateOf { state.pagerState.ordinal } }
