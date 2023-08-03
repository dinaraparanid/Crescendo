package com.paranid5.mediastreamer.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.about_app.AboutApp
import com.paranid5.mediastreamer.presentation.audio_effects.AudioEffectsScreen
import com.paranid5.mediastreamer.presentation.audio_effects.AudioEffectsViewModel
import com.paranid5.mediastreamer.presentation.composition_locals.LocalActivity
import com.paranid5.mediastreamer.presentation.composition_locals.LocalNavController
import com.paranid5.mediastreamer.presentation.favourites.FavouritesScreen
import com.paranid5.mediastreamer.presentation.fetch_stream.SearchStreamScreen
import com.paranid5.mediastreamer.presentation.fetch_stream.FetchStreamViewModel
import com.paranid5.mediastreamer.presentation.playing.PlayingScreen
import com.paranid5.mediastreamer.presentation.playing.PlayingViewModel
import com.paranid5.mediastreamer.presentation.settings.SettingsScreen
import com.paranid5.mediastreamer.presentation.track_collections.AlbumsScreen
import com.paranid5.mediastreamer.presentation.tracks.TracksScreen
import com.paranid5.mediastreamer.presentation.tracks.TracksViewModel
import com.paranid5.mediastreamer.presentation.ui.utils.OnBackPressedHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun ContentScreen(
    padding: PaddingValues,
    curScreenState: MutableStateFlow<Screens>,
    fetchStreamViewModel: FetchStreamViewModel = koinViewModel(),
    audioEffectsViewModel: AudioEffectsViewModel = koinViewModel(),
    tracksViewModel: TracksViewModel = koinViewModel()
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
        startDestination = Screens.Tracks.title,
        modifier = Modifier.padding(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding()
        )
    ) {
        composable(route = Screens.Tracks.title) {
            TracksScreen(
                tracksViewModel = tracksViewModel,
                curScreenState = curScreenState,
                modifier = Modifier.fillMaxSize().padding(10.dp)
            )
        }

        composable(route = Screens.TrackCollections.Albums.title) {
            AlbumsScreen(curScreenState)
        }

        composable(route = Screens.StreamFetching.title) {
            SearchStreamScreen(fetchStreamViewModel, curScreenState)
        }

        composable(route = Screens.Audio.AudioEffects.title) {
            AudioEffectsScreen(audioEffectsViewModel, curScreenState)
        }

        composable(route = Screens.AboutApp.title) {
            AboutApp(curScreenState)
        }

        composable(route = Screens.Favourites.title) {
            FavouritesScreen(curScreenState)
        }

        composable(route = Screens.Settings.title) {
            SettingsScreen(curScreenState)
        }
    }
}