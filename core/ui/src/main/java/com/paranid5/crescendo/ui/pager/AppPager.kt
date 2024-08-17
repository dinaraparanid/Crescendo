package com.paranid5.crescendo.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme
import com.paranid5.crescendo.utils.extensions.simpleShadow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

private val PagerElevation = 4.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppPager(
    pagerUiStates: ImmutableList<PagerUiState>,
    activePage: Int,
    modifier: Modifier = Modifier,
    onActivePageChanged: (targetPage: Int) -> Unit,
    pageContent: @Composable PagerScope.(page: Int) -> Unit,
) {
    val pagesAmount by rememberPagesAmount(pagerUiStates = pagerUiStates)
    val pagerState = rememberPagerState { pagesAmount }
    val coroutineScope = rememberCoroutineScope()

    val appCorners = AppTheme.dimensions.corners
    val pagerShape = remember(appCorners) {
        RoundedCornerShape(appCorners.extraMedium)
    }

    LaunchedEffect(pagerState.targetPage) {
        onActivePageChanged(pagerState.targetPage)
    }

    Column(modifier) {
        AppPagerTabs(
            pagerUiStates = pagerUiStates,
            activePage = activePage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimensions.padding.extraMedium)
                .simpleShadow(
                    elevation = PagerElevation,
                    shape = pagerShape,
                )
                .clip(pagerShape),
        ) { page ->
            onActivePageChanged(page)
            coroutineScope.launch { pagerState.animateScrollToPage(page = page) }
        }

        Spacer(Modifier.height(AppTheme.dimensions.padding.medium))

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            pageSpacing = AppTheme.dimensions.padding.extraMedium,
            contentPadding = PaddingValues(horizontal = AppTheme.dimensions.padding.extraMedium),
            pageContent = pageContent,
        )
    }
}

@Composable
internal fun rememberPagesAmount(pagerUiStates: ImmutableList<PagerUiState>) =
    remember(pagerUiStates) { derivedStateOf { pagerUiStates.size } }
