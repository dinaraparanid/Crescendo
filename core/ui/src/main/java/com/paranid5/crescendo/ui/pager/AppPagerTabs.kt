package com.paranid5.crescendo.ui.pager

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import com.paranid5.crescendo.utils.extensions.pxToDp
import kotlinx.collections.immutable.ImmutableList

private val PagerActiveZoneHeight = 4.dp

private val PagerTabPadding = 16.dp

@Composable
internal fun AppPagerTabs(
    pagerUiStates: ImmutableList<PagerUiState>,
    activePage: Int,
    modifier: Modifier = Modifier,
    onActivePageChanged: (targetPage: Int) -> Unit,
) {
    var pagerWidth by remember { mutableIntStateOf(1) }
    val pagesAmount by rememberPagesAmount(pagerUiStates = pagerUiStates)

    val tabWidthPx = remember(pagerWidth) { pagerWidth / pagesAmount }
    val tabWidth = tabWidthPx.pxToDp()

    val activeZoneWidth = remember(tabWidth) {
        maxOf(tabWidth - PagerTabPadding * 2, 1.dp)
    }

    Column(
        modifier
            .background(AppTheme.colors.background.highContrast)
            .onGloballyPositioned { coords ->
                pagerWidth = coords.size.width
            },
    ) {
        Spacer(Modifier.height(AppTheme.dimensions.padding.small))

        AppPagerTabsContent(
            pagerUiStates = pagerUiStates,
            modifier = Modifier.fillMaxWidth(),
            onActivePageChanged = onActivePageChanged,
        )

        Spacer(Modifier.height(AppTheme.dimensions.padding.small))

        AppPagerActiveZone(
            modifier = Modifier
                .width(activeZoneWidth)
                .height(PagerActiveZoneHeight)
                .activePagerTab(
                    activePage = activePage,
                    pagesAmount = pagesAmount,
                    pagerWidthPx = pagerWidth,
                ),
        )

        Spacer(Modifier.height(AppTheme.dimensions.padding.small))
    }
}

@Composable
private fun AppPagerTabsContent(
    pagerUiStates: ImmutableList<PagerUiState>,
    modifier: Modifier = Modifier,
    onActivePageChanged: (targetPage: Int) -> Unit,
) = Row(modifier = modifier) {
    Spacer(Modifier.height(PagerTabPadding))

    pagerUiStates.forEachIndexed { index, pager ->
        AppPagerTab(
            text = pager.title,
            modifier = Modifier
                .weight(1F)
                .clickableWithRipple { onActivePageChanged(index) },
        )

        Spacer(Modifier.height(PagerTabPadding))
    }
}

@Composable
private fun AppPagerTab(
    text: String,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    Text(
        text = text,
        color = AppTheme.colors.text.primary,
        style = AppTheme.typography.body,
        modifier = Modifier.align(Alignment.Center),
    )
}

@Composable
private fun AppPagerActiveZone(modifier: Modifier = Modifier) = Spacer(
    modifier = modifier.background(
        color = AppTheme.colors.selection.selected,
        shape = RoundedCornerShape(AppTheme.dimensions.corners.extraSmall),
    )
)

@Composable
private fun Modifier.activePagerTab(
    activePage: Int,
    pagesAmount: Int,
    pagerWidthPx: Int,
): Modifier {
    val xOffset = (activePage * pagerWidthPx / pagesAmount).pxToDp() + PagerTabPadding
    val xOffsetAnim by animateDpAsState(targetValue = xOffset, label = "")
    return this.offset(x = xOffsetAnim, y = 0.dp)
}
