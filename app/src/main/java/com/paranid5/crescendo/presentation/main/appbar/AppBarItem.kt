package com.paranid5.crescendo.presentation.main.appbar

import androidx.annotation.StringRes
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.presentation.main.Screens
import com.paranid5.crescendo.presentation.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.presentation.composition_locals.LocalNavController
import com.paranid5.crescendo.presentation.composition_locals.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun AppBarItem(
    @StringRes title: Int,
    image: ImageVector,
    screen: Screens,
    modifier: Modifier = Modifier,
) = AppBarItemInternal(
    icon = {
        Icon(
            imageVector = image,
            contentDescription = stringResource(id = title),
            tint = LocalAppColors.current.value.onBackground
        )
    },
    screen = screen,
    modifier = modifier
)

@Composable
fun AppBarItem(
    @StringRes title: Int,
    image: Painter,
    screen: Screens,
    modifier: Modifier = Modifier,
) = AppBarItemInternal(
    icon = {
        Icon(
            painter = image,
            contentDescription = stringResource(id = title),
            tint = LocalAppColors.current.value.onBackground
        )
    },
    screen = screen,
    modifier = modifier
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AppBarItemInternal(
    icon: @Composable () -> Unit,
    screen: Screens,
    modifier: Modifier = Modifier,
) {
    val navHostController = LocalNavController.current
    val playingSheetState = LocalPlayingSheetState.current
    val curPlaylistSheetState = LocalCurrentPlaylistSheetState.current

    val isEnabled = playingSheetState?.bottomSheetState?.isExpanded != true
            && curPlaylistSheetState?.isVisible != true

    IconButton(
        modifier = modifier,
        enabled = isEnabled,
        onClick = { navHostController.navigateIfNotSame(screen) }
    ) { icon() }
}