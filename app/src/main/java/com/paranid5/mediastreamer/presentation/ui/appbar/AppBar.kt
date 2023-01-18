package com.paranid5.mediastreamer.presentation.ui.appbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.ui.screens.Screens
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors

@Composable
fun AppBar(modifier: Modifier = Modifier) =
    BottomAppBar(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(color = LocalAppColors.current.value.primary)
    ) {
        AppBarItem(
            title = R.string.about_app,
            image = Icons.Filled.Info,
            screen = Screens.AboutApp,
            modifier = Modifier.weight(1F)
        )

        AppBarItem(
            title = R.string.favourites,
            image = Icons.Filled.Favorite,
            screen = Screens.Favourite,
            modifier = Modifier.weight(1F)
        )

        AppBarItem(
            title = R.string.settings,
            image = Icons.Filled.Settings,
            screen = Screens.Settings,
            modifier = Modifier.weight(1F)
        )
    }