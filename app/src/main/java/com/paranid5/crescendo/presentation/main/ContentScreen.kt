package com.paranid5.crescendo.presentation.main

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paranid5.crescendo.R
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.composition_locals.LocalActivity
import com.paranid5.crescendo.presentation.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.presentation.composition_locals.LocalNavController
import com.paranid5.crescendo.presentation.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.main.about_app.AboutApp
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsScreen
import com.paranid5.crescendo.presentation.main.favourites.FavouritesScreen
import com.paranid5.crescendo.presentation.main.fetch_stream.FetchStreamScreen
import com.paranid5.crescendo.presentation.main.settings.SettingsScreen
import com.paranid5.crescendo.presentation.main.track_collections.AlbumsScreen
import com.paranid5.crescendo.presentation.main.tracks.TracksScreen
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerScreen
import com.paranid5.crescendo.presentation.ui.utils.OnBackPressedHandler
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentScreen(
    padding: PaddingValues,
    viewModel: MainActivityViewModel = koinActivityViewModel(),
) {
    val backPressedCounter = AtomicInteger()
    val activity = LocalActivity.current
    val playingSheetState = LocalPlayingSheetState.current
    val currentPlaylistSheetState = LocalCurrentPlaylistSheetState.current
    val layoutDirection = LocalLayoutDirection.current

    OnBackPressedHandler { isStackEmpty ->
        if (currentPlaylistSheetState?.isVisible == true) {
            currentPlaylistSheetState.hide()
            return@OnBackPressedHandler
        }

        if (playingSheetState?.bottomSheetState?.isExpanded == true) {
            playingSheetState.bottomSheetState.collapse()
            return@OnBackPressedHandler
        }

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
        startDestination = Screens.Tracks.title,
        modifier = Modifier.padding(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding(),
            start = padding.calculateStartPadding(layoutDirection),
            end = padding.calculateEndPadding(layoutDirection)
        )
    ) {
        composable(route = Screens.Tracks.title) {
            viewModel.setCurScreen(Screens.Tracks)

            TracksScreen(
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = topPadding,
                        start = 8.dp,
                        end = endPadding
                    )
            )
        }

        composable(route = Screens.TrackCollections.Albums.title) {
            viewModel.setCurScreen(Screens.TrackCollections.Albums)
            AlbumsScreen()
        }

        composable(route = Screens.StreamFetching.title) {
            viewModel.setCurScreen(Screens.StreamFetching)
            FetchStreamScreen(Modifier.fillMaxSize())
        }

        composable(route = Screens.Audio.AudioEffects.title) {
            viewModel.setCurScreen(Screens.Audio.AudioEffects)

            AudioEffectsScreen(
                Modifier.padding(
                    top = topPadding,
                    start = 8.dp,
                    end = endPadding
                )
            )
        }

        composable(route = Screens.Audio.Trimmer.title) {
            viewModel.setCurScreen(Screens.Audio.Trimmer)
            TrimmerScreen(
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = topPadding,
                        start = 8.dp,
                        end = endPadding
                    )
            )
        }

        composable(route = Screens.AboutApp.title) {
            viewModel.setCurScreen(Screens.AboutApp)
            AboutApp()
        }

        composable(route = Screens.Favourites.title) {
            viewModel.setCurScreen(Screens.Favourites)
            FavouritesScreen()
        }

        composable(route = Screens.Settings.title) {
            viewModel.setCurScreen(Screens.Settings)
            SettingsScreen()
        }
    }
}

private inline val topPadding
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 16.dp
        else -> 48.dp
    }

private inline val endPadding
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 40.dp
        else -> 8.dp
    }