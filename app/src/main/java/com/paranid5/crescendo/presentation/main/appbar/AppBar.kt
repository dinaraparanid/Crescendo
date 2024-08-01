package com.paranid5.crescendo.presentation.main.appbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        containerColor = colors.primary,
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = dimensions.corners.extraMedium,
                    topEnd = dimensions.corners.extraMedium,
                )
            )
    ) {
        val itemModifier = Modifier
            .weight(1F)
            .padding(vertical = dimensions.padding.extraSmall)

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
