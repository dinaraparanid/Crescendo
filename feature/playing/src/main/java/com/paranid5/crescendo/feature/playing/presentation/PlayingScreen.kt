package com.paranid5.crescendo.feature.playing.presentation

import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.feature.playing.presentation.effect.LifecycleEffect
import com.paranid5.crescendo.feature.playing.presentation.effect.LoadCoverWithPaletteEffect
import com.paranid5.crescendo.feature.playing.presentation.effect.PlayingScreenEffect
import com.paranid5.crescendo.feature.playing.presentation.effect.UpdateUiParamsEffect
import com.paranid5.crescendo.feature.playing.presentation.ui.PlayingScreenLandscape
import com.paranid5.crescendo.feature.playing.presentation.ui.PlayingScreenPortrait
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalCoverAlpha
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.view_model.PlayingScreenEffect
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

    var cover by remember { mutableStateOf<BitmapDrawable?>(null) }
    var palette by remember { mutableStateOf<Palette?>(null) }

    LifecycleEffect(onUiIntent = onUiIntent)

    UpdateUiParamsEffect(
        screenPlaybackStatus = screenPlaybackStatus,
        onUiIntent = onUiIntent,
    )

    PlayingScreenEffect(effectFlow = viewModel.effectFlow, onScreenEffect = onScreenEffect)

    LoadCoverWithPaletteEffect(
        viewModel = viewModel,
        state = state,
        screenPlaybackStatus = screenPlaybackStatus,
    ) { (cv, plt) ->
        cover = cv
        palette = plt
    }

    CompositionLocalProvider(
        LocalCoverAlpha provides coverAlpha,
        LocalPalette provides palette,
    ) {
        when (config.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> PlayingScreenLandscape(
                screenPlaybackStatus = screenPlaybackStatus,
                state = state,
                coverBitmap = cover,
                onUiIntent = onUiIntent,
                modifier = modifier,
            )

            else -> PlayingScreenPortrait(
                screenPlaybackStatus = screenPlaybackStatus,
                state = state,
                coverBitmap = cover,
                onUiIntent = onUiIntent,
                modifier = modifier,
            )
        }
    }
}
