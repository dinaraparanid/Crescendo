package com.paranid5.crescendo.presentation.main.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.paranid5.crescendo.core.common.navigation.LocalNavigator
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.navigation.AppScreen
import com.paranid5.crescendo.navigation.requireAppNavigator
import com.paranid5.crescendo.ui.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun AppBarItem(
    title: String,
    icon: ImageVector,
    screen: AppScreen,
    modifier: Modifier = Modifier,
) {
    val navigator = LocalNavigator.requireAppNavigator()
    val playingSheetState = LocalPlayingSheetState.current
    val curPlaylistSheetState = LocalCurrentPlaylistSheetState.current

    val isEnabled by remember(playingSheetState, curPlaylistSheetState) {
        derivedStateOf {
            playingSheetState?.bottomSheetState?.isExpanded != true
                    && curPlaylistSheetState?.isVisible != true
        }
    }

    val currentScreen by navigator.currentScreenState.collectLatestAsState()

    val isScreenCurrent by remember(currentScreen, screen) {
        derivedStateOf { currentScreen == screen }
    }

    val itemColor by rememberItemColor(isScreenCurrent = isScreenCurrent)

    Box(
        modifier = modifier.clickableWithRipple(enabled = isEnabled) {
            navigator.pushIfNotSame(screen)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = itemColor,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(Modifier.weight(1F))

            Text(
                text = title,
                style = typography.regular,
                color = itemColor,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun rememberItemColor(isScreenCurrent: Boolean): State<Color> {
    val appColors = colors

    return remember(isScreenCurrent, appColors) {
        derivedStateOf {
            when {
                isScreenCurrent -> appColors.selection.selected
                else -> appColors.selection.notSelected
            }
        }
    }
}
