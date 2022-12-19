package com.paranid5.mediastreamer.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import com.paranid5.mediastreamer.R

sealed class Screens(
    @JvmField val title: String,
    val content: @Composable (NavBackStackEntry) -> Unit
) {
    object Home : Screens(
        title = "home",
        content = { HomeScreen() }
    )

    object AboutApp : Screens(
        title = "about_app",
        content = { AboutApp() }
    )

    object Favourite : Screens(
        title = "favourites",
        content = { FavouritesScreen() }
    )

    object Settings : Screens(
        title = "settings",
        content = { SettingsScreen() }
    )
}