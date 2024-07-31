package com.paranid5.crescendo.presentation.main

import android.app.Activity
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.paranid5.crescendo.about_app.AboutApp
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsScreen
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.favourites.FavouritesScreen
import com.paranid5.crescendo.fetch_stream.presentation.FetchStreamScreen
import com.paranid5.crescendo.navigation.LocalNavController
import com.paranid5.crescendo.navigation.Screens
import com.paranid5.crescendo.settings.SettingsScreen
import com.paranid5.crescendo.track_collections.AlbumsScreen
import com.paranid5.crescendo.tracks.presentation.TracksScreen
import com.paranid5.crescendo.trimmer.presentation.TrimmerScreen
import com.paranid5.crescendo.ui.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.ui.utils.OnBackPressed
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger

private const val CLICKS_FOR_EXIT = 2
private const val BACK_TOAST_DELAY = 500L

@Composable
fun ContentScreen(padding: PaddingValues) {
    val navigator = LocalNavController.current
    val layoutDirection = LocalLayoutDirection.current

    BackHandler()

    NavHost(
        navController = navigator.value!!,
        startDestination = Screens.Tracks.title,
        modifier = Modifier.padding(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding(),
            start = padding.calculateStartPadding(layoutDirection),
            end = padding.calculateEndPadding(layoutDirection)
        )
    ) {
        composable(route = Screens.Tracks.title) {
            navigator.setCurScreen(Screens.Tracks)

            TracksScreen(
                Modifier
                    .fillMaxSize()
                    .screenPaddingDefault()
            )
        }

        composable(route = Screens.TrackCollections.Albums.title) {
            navigator.setCurScreen(Screens.TrackCollections.Albums)

            AlbumsScreen(
                Modifier
                    .fillMaxSize()
                    .screenPaddingDefault()
            )
        }

        composable(route = Screens.StreamFetching.title) {
            navigator.setCurScreen(Screens.StreamFetching)

            FetchStreamScreen(
                Modifier
                    .fillMaxSize()
                    .screenPaddingDefault()
            )
        }

        composable(route = Screens.Audio.AudioEffects.title) {
            navigator.setCurScreen(Screens.Audio.AudioEffects)
            AudioEffectsScreen(Modifier.screenPaddingDefault())
        }

        composable(
            route = Screens.Audio.Trimmer.title,
            arguments = persistentListOf(
                navArgument(Screens.Audio.Trimmer.TRACK_PATH_KEY) {
                    type = NavType.StringType
                }
            )
        ) {
            navigator.setCurScreen(Screens.Audio.Trimmer)

            TrimmerScreen(
                backStackEntry = it,
                modifier = Modifier
                    .fillMaxSize()
                    .screenPaddingRequired()
            )
        }

        composable(route = Screens.AboutApp.title) {
            navigator.setCurScreen(Screens.AboutApp)

            AboutApp(
                Modifier
                    .fillMaxSize()
                    .screenPaddingDefault()
            )
        }

        composable(route = Screens.Favourites.title) {
            navigator.setCurScreen(Screens.Favourites)

            FavouritesScreen(
                Modifier
                    .fillMaxSize()
                    .screenPaddingDefault()
            )
        }

        composable(route = Screens.Settings.title) {
            navigator.setCurScreen(Screens.Settings)

            SettingsScreen(
                Modifier
                    .fillMaxSize()
                    .screenPaddingDefault()
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BackHandler() {
    val context = LocalContext.current
    val playingSheetState = LocalPlayingSheetState.current
    val currentPlaylistSheetState = LocalCurrentPlaylistSheetState.current
    var backPressedCounter by remember { mutableIntStateOf(0) }

    OnBackPressed { isStackEmpty ->
        if (currentPlaylistSheetState?.isVisible == true) {
            currentPlaylistSheetState.hide()
            return@OnBackPressed
        }

        if (playingSheetState?.bottomSheetState?.isExpanded == true) {
            playingSheetState.bottomSheetState.collapse()
            return@OnBackPressed
        }

        if (isStackEmpty) {
            when (++backPressedCounter) {
                CLICKS_FOR_EXIT -> (context as? Activity)?.finish()

                else -> {
                    delay(BACK_TOAST_DELAY)

                    Toast.makeText(
                        context,
                        R.string.press_twice_to_exit,
                        Toast.LENGTH_SHORT
                    ).show()

                    backPressedCounter = 0
                }
            }
        }
    }
}

@Composable
private fun Modifier.screenPaddingDefault() =
    this.padding(
        top = topPaddingDefault,
        start = 8.dp,
        end = endPaddingDefault
    )

@Composable
private fun Modifier.screenPaddingRequired() =
    this.padding(
        top = topPaddingRequired,
        start = startPaddingRequired,
        end = endPaddingRequired
    )

private inline val topPaddingDefault
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 16.dp
        else -> 48.dp
    }

private inline val endPaddingDefault
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 40.dp
        else -> 8.dp
    }

private inline val topPaddingRequired
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 16.dp
        else -> 48.dp
    }

private inline val startPaddingRequired
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 28.dp
        else -> 0.dp
    }

private inline val endPaddingRequired
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 40.dp
        else -> 0.dp
    }