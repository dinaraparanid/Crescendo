package com.paranid5.mediastreamer.ui.appbar

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.paranid5.mediastreamer.ui.screens.LocalNavController
import com.paranid5.mediastreamer.ui.screens.Screens
import com.paranid5.mediastreamer.ui.theme.LocalAppColors

data class AppBarItemData(
    @StringRes val title: Int,
    val image: ImageVector,
    val screen: Screens
)

@Composable
fun RowScope.AppBarItem(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    image: ImageVector,
    screen: Screens
) {
    val navHostController = LocalNavController.current

    IconButton(
        modifier = modifier.weight(1F),
        onClick = { navHostController.navigateIfNotSame(screen) }
    ) {
        Icon(
            imageVector = image,
            contentDescription = stringResource(id = title),
            tint = LocalAppColors.current.value.primary
        )
    }
}