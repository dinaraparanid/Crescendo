package com.paranid5.crescendo.feature.current_playlist.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.current_playlist.presentation.effect.LifecycleEffect
import com.paranid5.crescendo.feature.current_playlist.presentation.effect.ScreenEffect
import com.paranid5.crescendo.feature.current_playlist.presentation.ui.CurrentPlaylistBar
import com.paranid5.crescendo.feature.current_playlist.presentation.ui.CurrentPlaylistTrackList
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistScreenEffect
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistUiIntent
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistViewModel
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistViewModelImpl
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@NonRestartableComposable
@Composable
fun CurrentPlaylistScreen(
    modifier: Modifier = Modifier,
    viewModel: CurrentPlaylistViewModel = koinViewModel<CurrentPlaylistViewModelImpl>(),
    onScreenEffect: (CurrentPlaylistScreenEffect) -> Unit,
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    LifecycleEffect(onUiIntent = onUiIntent)

    ScreenEffect(state = state, onScreenEffect = onScreenEffect) {
        onUiIntent(CurrentPlaylistUiIntent.Screen.ClearScreenEffect)
    }

    Column(modifier) {
        Spacer(Modifier.height(topPadding))

        CurrentPlaylistBar(
            state = state,
            modifier = Modifier.fillMaxWidth().padding(
                start = dimensions.padding.extraMedium,
                end = dimensions.padding.extraMedium,
            ),
        )

        Spacer(Modifier.height(dimensions.padding.extraBig))

        CurrentPlaylistTrackList(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier.fillMaxWidth().padding(
                start = dimensions.padding.extraMedium,
                end = dimensions.padding.extraMedium,
            ),
        )

        Spacer(Modifier.height(dimensions.padding.extraBig))
    }
}

private inline val topPadding
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> dimensions.padding.small
        else -> dimensions.padding.large
    }
