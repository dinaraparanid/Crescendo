package com.paranid5.crescendo.feature.play.main.presentation.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.play.main.presentation.view_model.PlayState
import com.paranid5.crescendo.feature.play.main.presentation.view_model.PlayState.PagerState
import com.paranid5.crescendo.feature.play.main.presentation.view_model.PlayUiIntent
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

    Column(modifier) {
        PrimaryPagerTabs(
            activePage = state.pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onUiIntent(PlayUiIntent.UpdatePagerState(pagerState = it))
            coroutineScope.launch { pagerState.animateScrollToPage(page = it.ordinal) }
        }

        Spacer(Modifier.height(dimensions.padding.medium))

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
        ) { page ->
            val pageModifier = Modifier.fillMaxSize()

            when (PagerState.entries.getOrNull(page)) {
                PagerState.TRACKS -> Box(pageModifier) {
                    Text("TODO: Tracks", Modifier.align(Alignment.Center))
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