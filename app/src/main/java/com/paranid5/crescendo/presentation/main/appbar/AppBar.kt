package com.paranid5.crescendo.presentation.main.appbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.navigation.Screens

@Composable
fun AppBar(modifier: Modifier = Modifier) =
    BottomAppBar(
        modifier = modifier,
        containerColor = colors.primary,
        contentPadding = PaddingValues(vertical = dimensions.padding.extraSmall),
        windowInsets = WindowInsets(bottom = dimensions.padding.large),
    ) {
        val itemModifier = Modifier.weight(1F)

        AppBarItem(
            title = stringResource(R.string.tracks),
            icon = ImageVector.vectorResource(R.drawable.tracks),
            screen = Screens.Tracks,
            modifier = itemModifier,
        )

        AppBarItem(
            title = stringResource(R.string.streaming),
            icon = ImageVector.vectorResource(R.drawable.stream),
            screen = Screens.StreamFetching,
            modifier = itemModifier,
        )

        AppBarItem(
            title = stringResource(R.string.settings),
            icon = Icons.Filled.Settings,
            screen = Screens.Settings,
            modifier = itemModifier,
        )
    }
