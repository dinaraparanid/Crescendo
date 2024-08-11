package com.paranid5.crescendo.feature.play.main.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.paranid5.crescendo.core.common.navigation.LocalNavigator
import com.paranid5.crescendo.feature.play.favourites.FavouritesScreen
import com.paranid5.crescendo.feature.play.main.navigation.PlayScreen
import com.paranid5.crescendo.feature.play.main.navigation.requirePlayNavigator
import com.paranid5.crescendo.feature.play.main.view_model.PlayState
import com.paranid5.crescendo.feature.play.main.view_model.PlayUiIntent
import com.paranid5.crescendo.feature.play.playlists.PlaylistsScreen

@Composable
internal fun PlayHost(
    state: PlayState,
    onUiIntent: (PlayUiIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.requirePlayNavigator()

    navigator.navHost?.let {
        NavHost(
            navController = it,
            startDestination = PlayScreen.Primary.title,
            modifier = modifier,
        ) {
            composable(route = PlayScreen.Primary.title) {
                navigator.replaceIfNotSame(PlayScreen.Primary)

                PrimaryScreen(
                    state = state,
                    onUiIntent = onUiIntent,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(route = PlayScreen.Favourites.title) {
                navigator.replaceIfNotSame(PlayScreen.Favourites)
                FavouritesScreen(modifier = Modifier.fillMaxSize())
            }

            composable(route = PlayScreen.Playlists.title) {
                navigator.replaceIfNotSame(PlayScreen.Playlists)
                PlaylistsScreen(modifier = Modifier.fillMaxSize())
            }

            composable(route = PlayScreen.Recent.title) {
                navigator.replaceIfNotSame(PlayScreen.Recent)
                Box(Modifier.fillMaxSize()) {
                    Text("TODO: Recent Screen", Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
