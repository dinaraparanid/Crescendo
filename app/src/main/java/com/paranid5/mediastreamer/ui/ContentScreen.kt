package com.paranid5.mediastreamer.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paranid5.mediastreamer.ui.screens.LocalNavController
import com.paranid5.mediastreamer.ui.screens.Screens

@Composable
fun ContentScreen(padding: PaddingValues) {
    NavHost(
        navController = LocalNavController.current.value!!,
        startDestination = Screens.Home.title,
        modifier = Modifier.padding(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding()
        )
    ) {
        composable(
            route = Screens.Home.title,
            content = Screens.Home.content
        )

        composable(
            route = Screens.AboutApp.title,
            content = Screens.AboutApp.content
        )

        composable(
            route = Screens.Favourite.title,
            content = Screens.Favourite.content
        )

        composable(
            route = Screens.Settings.title,
            content = Screens.Settings.content
        )
    }
}