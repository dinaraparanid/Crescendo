package com.paranid5.crescendo.feature.play.main.presentation.ui.pager

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.play.main.view_model.PlayState.PagerState
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import com.paranid5.crescendo.utils.extensions.pxToDp

private val PagerActiveZoneHeight = 4.dp

private val PagerTabPadding = 16.dp

@Composable
internal fun PrimaryPagerTabs(
    activePage: PagerState,
    modifier: Modifier = Modifier,
    onTabClick: (PagerState) -> Unit,
) {
    var pagerWidth by remember { mutableIntStateOf(1) }

    val activeZoneWidth = maxOf(
        (pagerWidth / PagesAmount).pxToDp() - PagerTabPadding * 2, 1.dp
    )

    Column(
        modifier
            .background(colors.background.highContrast)
            .onGloballyPositioned { coords ->
                pagerWidth = coords.size.width
            },
    ) {
        Spacer(Modifier.height(dimensions.padding.small))

        PrimaryPagerTabsContent(
            onTabClick = onTabClick,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(dimensions.padding.small))

        PrimaryPagerActiveZone(
            modifier = Modifier
                .width(activeZoneWidth)
                .height(PagerActiveZoneHeight)
                .activePagerTab(activePage = activePage, pagerWidthPx = pagerWidth),
        )

        Spacer(Modifier.height(dimensions.padding.small))
    }
}

@Composable
private fun PrimaryPagerTabsContent(
    modifier: Modifier = Modifier,
    onTabClick: (PagerState) -> Unit,
) = Row(modifier = modifier) {
    Spacer(Modifier.height(PagerTabPadding))

    PrimaryPagerTab(
        text = stringResource(R.string.tracks),
        modifier = Modifier
            .weight(1F)
            .clickableWithRipple { onTabClick(PagerState.TRACKS) },
    )

    Spacer(Modifier.height(PagerTabPadding))

    PrimaryPagerTab(
        text = stringResource(R.string.artists),
        modifier = Modifier
            .weight(1F)
            .clickableWithRipple { onTabClick(PagerState.ARTISTS) },
    )

    Spacer(Modifier.height(PagerTabPadding))

    PrimaryPagerTab(
        text = stringResource(R.string.albums),
        modifier = Modifier
            .weight(1F)
            .clickableWithRipple { onTabClick(PagerState.ALBUMS) },
    )

    Spacer(Modifier.height(PagerTabPadding))
}

@Composable
private fun PrimaryPagerTab(
    text: String,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    Text(
        text = text,
        color = colors.text.primary,
        style = typography.body,
        modifier = Modifier.align(Alignment.Center),
    )
}

@Composable
private fun PrimaryPagerActiveZone(modifier: Modifier = Modifier) = Spacer(
    modifier = modifier.background(
        color = colors.selection.selected,
        shape = RoundedCornerShape(dimensions.corners.extraSmall),
    )
)

@Composable
private fun Modifier.activePagerTab(
    activePage: PagerState,
    pagerWidthPx: Int,
): Modifier {
    val xOffset = (activePage.ordinal * pagerWidthPx / PagesAmount).pxToDp() + PagerTabPadding
    val xOffsetAnim by animateDpAsState(targetValue = xOffset, label = "")
    return this.offset(x = xOffsetAnim, y = 0.dp)
}
