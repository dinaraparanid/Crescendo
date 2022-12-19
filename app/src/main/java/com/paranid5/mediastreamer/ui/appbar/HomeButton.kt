package com.paranid5.mediastreamer.ui.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.ui.screens.LocalNavController
import com.paranid5.mediastreamer.ui.screens.Screens
import com.paranid5.mediastreamer.ui.theme.LocalAppColors

@Composable
fun HomeButton() {
    val navHostController = LocalNavController.current

    FloatingActionButton(onClick = { navHostController.navigateIfNotSame(Screens.Home) }) {
        Icon(
            imageVector = Icons.Filled.Home,
            contentDescription = stringResource(id = R.string.home),
            tint = LocalAppColors.current.value.background
        )
    }
}