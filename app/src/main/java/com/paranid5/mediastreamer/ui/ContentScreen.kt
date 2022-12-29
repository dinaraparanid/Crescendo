package com.paranid5.mediastreamer.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paranid5.mediastreamer.composition_locals.LocalStreamState
import com.paranid5.mediastreamer.composition_locals.screen
import com.paranid5.mediastreamer.ui.screens.*
import com.paranid5.mediastreamer.view_models.SearchStreamViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ContentScreen(
    padding: PaddingValues,
    viewModel: SearchStreamViewModel = koinViewModel()
) {
    val streamingScreen = LocalStreamState.current.screen

    NavHost(
        navController = LocalNavController.current.value!!,
        startDestination = Screens.StreamScreen.Searching.title,
        modifier = Modifier.padding(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding()
        )
    ) {
        composable(
            route = streamingScreen.title,
            content = { SearchStreamScreen(viewModel = viewModel) }
        )

        composable(
            route = Screens.AboutApp.title,
            content = { AboutApp() }
        )

        composable(
            route = Screens.Favourite.title,
            content = { FavouritesScreen() }
        )

        composable(
            route = Screens.Settings.title,
            content = { SettingsScreen() }
        )
    }
}