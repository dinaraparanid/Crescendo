package com.paranid5.mediastreamer.presentation.appbar

import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.composition_locals.LocalNavController
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors

@Composable
fun AppBarItem(
    @StringRes title: Int,
    image: ImageVector,
    screen: Screens,
    modifier: Modifier = Modifier,
) {
    val navHostController = LocalNavController.current

    IconButton(
        modifier = modifier,
        onClick = { navHostController.navigateIfNotSame(screen) }
    ) {
        Icon(
            imageVector = image,
            contentDescription = stringResource(id = title),
            tint = LocalAppColors.current.value.primary
        )
    }
}