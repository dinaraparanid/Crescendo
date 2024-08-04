package com.paranid5.crescendo.feature.current_playlist.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.current_playlist.presentation.effects.SubscribeOnBackEventsEffect
import com.paranid5.crescendo.feature.current_playlist.presentation.effects.SubscribeOnLifecycleEffect
import com.paranid5.crescendo.feature.current_playlist.presentation.views.CurrentPlaylistBar
import com.paranid5.crescendo.feature.current_playlist.presentation.views.CurrentPlaylistTrackList
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistBackResult
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistViewModel
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistViewModelImpl
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@NonRestartableComposable
@Composable
fun CurrentPlaylistScreen(
    modifier: Modifier = Modifier,
    viewModel: CurrentPlaylistViewModel = koinViewModel<CurrentPlaylistViewModelImpl>(),
    onBack: (CurrentPlaylistBackResult) -> Unit,
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    SubscribeOnLifecycleEffect(onUiIntent = onUiIntent)
    SubscribeOnBackEventsEffect(state = state, onBack = onBack)

    Column(modifier) {
        CurrentPlaylistBar(
            state = state,
            modifier = Modifier.fillMaxWidth().padding(
                top = topPadding,
                bottom = dimensions.padding.small,
                start = dimensions.padding.extraMedium,
                end = dimensions.padding.extraMedium,
            ),
        )

        CurrentPlaylistTrackList(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier.fillMaxWidth().padding(
                top = dimensions.padding.extraMedium,
                start = dimensions.padding.extraMedium,
                end = dimensions.padding.extraMedium,
                bottom = dimensions.padding.small,
            ),
        )
    }
}

private inline val topPadding
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> dimensions.padding.small
        else -> dimensions.padding.large
    }
