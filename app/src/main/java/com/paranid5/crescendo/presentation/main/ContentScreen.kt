package com.paranid5.crescendo.presentation.main

import android.app.Activity
import android.content.res.Configuration
import android.widget.Toast
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsScreen
import com.paranid5.crescendo.core.common.navigation.LocalNavigator
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.play.main.presentation.PlayScreen
import com.paranid5.crescendo.feature.play.main.view_model.PlayBackResult
import com.paranid5.crescendo.feature.preferences.PreferencesScreen
import com.paranid5.crescendo.feature.stream.presentation.StreamScreen
import com.paranid5.crescendo.navigation.AppNavigator
import com.paranid5.crescendo.navigation.AppScreen
import com.paranid5.crescendo.navigation.requireAppNavigator
import com.paranid5.crescendo.trimmer.presentation.TrimmerScreen
import com.paranid5.crescendo.ui.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.ui.utils.OnBackPressed
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay

private const val CLICKS_FOR_EXIT = 2
private const val BACK_TOAST_DELAY = 500L

@Composable
internal fun ContentScreen(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.requireAppNavigator()

    BackHandler()

    navigator.navHost?.let {
        ContentScreenNavHost(
            navigator = navigator,
            navHostController = it,
            modifier = modifier,
            screenModifier = Modifier
                .fillMaxSize()
                .screenPaddingDefault()
        )
    }
}

@Composable
private fun ContentScreenNavHost(
    navigator: AppNavigator,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
) = NavHost(
    navController = navHostController,
    startDestination = AppScreen.Play.title,
    modifier = modifier,
) {
    composable(route = AppScreen.Play.title) {
        navigator.updateCurrentScreen(AppScreen.Play)
        PlayScreen(modifier = screenModifier) { result ->
            when (result) {
                is PlayBackResult.ShowTrimmer ->
                    navigator.pushIfNotSame(AppScreen.Audio.Trimmer(result.trackUri))
            }
        }
    }

    composable(route = AppScreen.StreamFetching.title) {
        navigator.updateCurrentScreen(AppScreen.StreamFetching)
        StreamScreen(modifier = screenModifier)
    }

    composable(route = AppScreen.Audio.AudioEffects.title) {
        navigator.updateCurrentScreen(AppScreen.Audio.AudioEffects)
        AudioEffectsScreen(modifier = Modifier.screenPaddingDefault())
    }

    composable(
        route = AppScreen.Audio.Trimmer.title,
        arguments = persistentListOf(
            navArgument(AppScreen.Audio.Trimmer.TrackPathKey) {
                type = NavType.StringType
            }
        )
    ) {
        navigator.updateCurrentScreen(AppScreen.Audio.Trimmer)

        TrimmerScreen(
            backStackEntry = it,
            modifier = screenModifier,
        )
    }

    composable(route = AppScreen.Preferences.title) {
        navigator.updateCurrentScreen(AppScreen.Preferences)
        PreferencesScreen(modifier = screenModifier)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BackHandler() {
    val context = LocalContext.current
    val navigator = LocalNavigator.requireAppNavigator()
    val playingSheetState = LocalPlayingSheetState.current
    val currentPlaylistSheetState = LocalCurrentPlaylistSheetState.current
    var backPressedCounter by remember { mutableIntStateOf(0) }

    OnBackPressed(navigator = navigator) { isStackEmpty ->
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
        end = endPaddingDefault,
    )

private inline val topPaddingDefault
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> dimensions.padding.extraMedium
        else -> dimensions.padding.extraLarge
    }

private inline val endPaddingDefault
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> dimensions.padding.extraLarge
        else -> dimensions.padding.zero
    }
