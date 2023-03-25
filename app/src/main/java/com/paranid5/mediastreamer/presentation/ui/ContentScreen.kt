package com.paranid5.mediastreamer.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paranid5.mediastreamer.presentation.LocalNavController
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.about_app.AboutApp
import com.paranid5.mediastreamer.presentation.favourites.FavouritesScreen
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamScreen
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamViewModel
import com.paranid5.mediastreamer.presentation.streaming.StreamingScreen
import com.paranid5.mediastreamer.presentation.streaming.StreamingViewModel
import com.paranid5.mediastreamer.presentation.ui.screens.SettingsScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ContentScreen(
    padding: PaddingValues,
    searchStreamViewModel: SearchStreamViewModel = koinViewModel(),
    streamingViewModel: StreamingViewModel = koinViewModel(),
) {
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
            StreamingScreen(viewModel = streamingViewModel)
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