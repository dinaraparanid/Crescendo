package com.paranid5.crescendo.feature.play.main.presentation.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.play.main.presentation.view_model.PlayState
import com.paranid5.crescendo.feature.play.main.presentation.view_model.PlayState.PagerState
import com.paranid5.crescendo.feature.play.main.presentation.view_model.PlayUiIntent
import com.paranid5.crescendo.tracks.presentation.TracksScreen
import com.paranid5.crescendo.tracks.view_model.TracksBackResult
import kotlinx.coroutines.launch

internal const val PagesAmount = 3

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PrimaryPager(
    state: PlayState,
    onUiIntent: (PlayUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { PagesAmount }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onUiIntent(PlayUiIntent.UpdatePagerState(PagerState.entries[pagerState.currentPage]))
    }

    Column(modifier) {
        PrimaryPagerTabs(
            activePage = state.pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.padding.extraMedium),
        ) {
            onUiIntent(PlayUiIntent.UpdatePagerState(pagerState = it))
            coroutineScope.launch { pagerState.animateScrollToPage(page = it.ordinal) }
        }

        Spacer(Modifier.height(dimensions.padding.medium))

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            pageSpacing = dimensions.padding.extraMedium,
            contentPadding = PaddingValues(horizontal = dimensions.padding.extraMedium),
        ) { page ->
            val pageModifier = Modifier.fillMaxSize()

            when (PagerState.entries.getOrNull(page)) {
                PagerState.TRACKS -> TracksScreen(pageModifier) { result ->
                    when (result) {
                        is TracksBackResult.ShowTrimmer -> {
                            // TODO: Show trimmer
                        }
                    }
                }

                PagerState.ARTISTS -> Box(pageModifier) {
                    Text("TODO: Artists", Modifier.align(Alignment.Center))
                }

                PagerState.ALBUMS -> Box(pageModifier) {
                    Text("TODO: Albums", Modifier.align(Alignment.Center))
                }

                null -> error("Illegal page number: $page")
            }
        }
    }
}