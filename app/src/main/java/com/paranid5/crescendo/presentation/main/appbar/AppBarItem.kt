package com.paranid5.crescendo.presentation.main.appbar

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.paranid5.crescendo.presentation.main.Screens
import com.paranid5.crescendo.presentation.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.presentation.composition_locals.LocalNavController
import com.paranid5.crescendo.presentation.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.ui.LocalAppColors

@Composable
fun AppBarItem(
    title: String,
    image: ImageVector,
    screen: Screens,
    modifier: Modifier = Modifier,
) = AppBarItemInternal(
    screen = screen,
    modifier = modifier,
    icon = { AppBarIcon(title, image) }
)

@Composable
fun AppBarItem(
    title: String,
    image: Painter,
    screen: Screens,
    modifier: Modifier = Modifier,
) = AppBarItemInternal(
    screen = screen,
    modifier = modifier,
    icon = { AppBarIcon(title, image) }
)

@Composable
private fun AppBarIcon(
    title: String,
    image: ImageVector,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Icon(
        imageVector = image,
        contentDescription = title,
        tint = colors.backgroundAlternative,
        modifier = modifier
    )
}

@Composable
private fun AppBarIcon(
    title: String,
    image: Painter,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Icon(
        painter = image,
        contentDescription = title,
        tint = colors.backgroundAlternative,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AppBarItemInternal(
    screen: Screens,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navHostController = LocalNavController.current
    val playingSheetState = LocalPlayingSheetState.current
    val curPlaylistSheetState = LocalCurrentPlaylistSheetState.current

    val isEnabled by remember(playingSheetState, curPlaylistSheetState) {
        derivedStateOf {
            playingSheetState?.bottomSheetState?.isExpanded != true
                    && curPlaylistSheetState?.isVisible != true
        }
    }

    IconButton(
        modifier = modifier,
        enabled = isEnabled,
        onClick = { navHostController.navigateIfNotSame(screen) }
    ) { icon() }
}