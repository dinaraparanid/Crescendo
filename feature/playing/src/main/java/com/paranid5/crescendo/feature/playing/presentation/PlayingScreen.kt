package com.paranid5.crescendo.feature.playing.presentation

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.feature.playing.presentation.effect.LifecycleEffect
import com.paranid5.crescendo.feature.playing.presentation.effect.ScreenEffect
import com.paranid5.crescendo.feature.playing.presentation.effect.UpdateUiParamsEffect
import com.paranid5.crescendo.feature.playing.presentation.ui.PlayingScreenLandscape
import com.paranid5.crescendo.feature.playing.presentation.ui.PlayingScreenPortrait
import com.paranid5.crescendo.feature.playing.view_model.PlayingScreenEffect
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent
import com.paranid5.crescendo.feature.playing.view_model.PlayingViewModel
import com.paranid5.crescendo.feature.playing.view_model.PlayingViewModelImpl
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@NonRestartableComposable
@Composable
fun PlayingScreen(
    screenPlaybackStatus: PlaybackStatus,
    coverAlpha: Float,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinViewModel<PlayingViewModelImpl>(),
    onScreenEffect: (PlayingScreenEffect) -> Unit,
) {
    val config = LocalConfiguration.current
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    LifecycleEffect(onUiIntent = onUiIntent)

    UpdateUiParamsEffect(
        screenPlaybackStatus = screenPlaybackStatus,
        coverAlpha = coverAlpha,
        onUiIntent = onUiIntent,
    )

    ScreenEffect(state = state, onScreenEffect = onScreenEffect) {
        onUiIntent(PlayingUiIntent.ScreenEffect.ClearScreenEffect)
    }

    when (config.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> PlayingScreenLandscape(
            state = state,
            onUiIntent = onUiIntent,
            modifier = modifier,
        )

        else -> PlayingScreenPortrait(
            state = state,
            onUiIntent = onUiIntent,
            modifier = modifier,
        )
    }
}
