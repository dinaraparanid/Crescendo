package com.paranid5.crescendo.presentation.main.appbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.navigation.AppScreen

private val AppBarItemHeight = 52.dp

@Composable
internal fun AppBar(modifier: Modifier = Modifier) =
    BottomAppBar(
        modifier = modifier,
        containerColor = colors.primary,
        contentPadding = PaddingValues(top = dimensions.padding.extraBig),
        windowInsets = WindowInsets(0),
    ) {
        val itemModifier = Modifier
            .weight(1F)
            .align(Alignment.Top)
            .height(AppBarItemHeight)

        AppBarItem(
            title = stringResource(R.string.appbar_play),
            icon = ImageVector.vectorResource(R.drawable.tab_play),
            screen = AppScreen.Play,
            modifier = itemModifier,
        )

        AppBarItem(
            title = stringResource(R.string.appbar_stream),
            icon = ImageVector.vectorResource(R.drawable.tab_stream),
            screen = AppScreen.StreamFetching,
            modifier = itemModifier,
        )

        AppBarItem(
            title = stringResource(R.string.appbar_preferences),
            icon = ImageVector.vectorResource(R.drawable.tab_preferences),
            modifier = itemModifier,
            screen = AppScreen.Preferences,
        )
    }
