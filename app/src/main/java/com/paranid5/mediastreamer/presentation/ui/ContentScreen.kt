package com.paranid5.mediastreamer.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.about_app.AboutApp
import com.paranid5.mediastreamer.presentation.composition_locals.LocalActivity
import com.paranid5.mediastreamer.presentation.composition_locals.LocalNavController
import com.paranid5.mediastreamer.presentation.favourites.FavouritesScreen
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamScreen
import com.paranid5.mediastreamer.presentation.search_stream.SearchStreamViewModel
import com.paranid5.mediastreamer.presentation.streaming.StreamingScreen
import com.paranid5.mediastreamer.presentation.streaming.StreamingViewModel
import com.paranid5.mediastreamer.presentation.ui.screens.SettingsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun ContentScreen(
    padding: PaddingValues,
    curScreenState: MutableStateFlow<Screens>,
    searchStreamViewModel: SearchStreamViewModel = koinViewModel(),
    streamingViewModel: StreamingViewModel = koinViewModel(),
) {
    val backPressedCounter = AtomicInteger()
    val activity = LocalActivity.current

    OnBackPressedHandler { isStackEmpty ->
        if (isStackEmpty) {
            when (backPressedCounter.incrementAndGet()) {
                2 -> activity?.finish()

                else -> {
                    Toast.makeText(
                        activity?.applicationContext,
                        R.string.press_twice_to_exit,
                        Toast.LENGTH_SHORT
                    ).show()

                    delay(500L)
                    backPressedCounter.set(0)
                }
            }
        }
    }

    NavHost(
        navController = LocalNavController.current.value!!,
        startDestination = Screens.StreamScreen.Searching.title,
        modifier = Modifier.padding(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding()
        )
    ) {
        composable(route = Screens.StreamScreen.Searching.title) {
            SearchStreamScreen(viewModel = searchStreamViewModel, curScreenState)
        }

        composable(route = Screens.StreamScreen.Streaming.title) {
            StreamingScreen(viewModel = streamingViewModel, curScreenState)
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