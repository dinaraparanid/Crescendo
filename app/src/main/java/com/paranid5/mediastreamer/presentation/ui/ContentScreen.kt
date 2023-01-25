package com.paranid5.mediastreamer.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paranid5.mediastreamer.presentation.composition_locals.LocalStreamState
import com.paranid5.mediastreamer.presentation.composition_locals.screen
import com.paranid5.mediastreamer.presentation.ui.screens.*
import com.paranid5.mediastreamer.presentation.view_models.SearchStreamViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ContentScreen(
    padding: PaddingValues,
    searchStreamViewModel: SearchStreamViewModel = koinViewModel()
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
        composable(route = Screens.StreamScreen.Searching.title) {
            SearchStreamScreen(viewModel = searchStreamViewModel)
        }

        composable(route = Screens.StreamScreen.Streaming.title) {
            StreamingScreen()
        }

        composable(route = Screens.AboutApp.title) {
            AboutApp()
        }

        composable(route = Screens.Favourite.title) {
            FavouritesScreen()
        }

        composable(route = Screens.Settings.title) {
            SettingsScreen()
        }
    }
}