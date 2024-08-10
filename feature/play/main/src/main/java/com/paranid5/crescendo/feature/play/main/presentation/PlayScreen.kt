package com.paranid5.crescendo.feature.play.main.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.paranid5.crescendo.core.common.navigation.LocalNavigator
import com.paranid5.crescendo.feature.play.main.navigation.PlayNavigator
import com.paranid5.crescendo.feature.play.main.presentation.effect.BackResultEffect
import com.paranid5.crescendo.feature.play.main.presentation.ui.PlayHost
import com.paranid5.crescendo.feature.play.main.view_model.PlayBackResult
import com.paranid5.crescendo.feature.play.main.view_model.PlayUiIntent
import com.paranid5.crescendo.feature.play.main.view_model.PlayViewModel
import com.paranid5.crescendo.feature.play.main.view_model.PlayViewModelImpl
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayViewModel = koinViewModel<PlayViewModelImpl>(),
    onBack: (PlayBackResult) -> Unit,
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    val navHost = rememberNavController()
    val navigator = remember(navHost) { PlayNavigator(navHost) }

    BackResultEffect(state = state, onBack = onBack) {
        onUiIntent(PlayUiIntent.ClearBackResult)
    }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        PlayHost(
            state = state,
            onUiIntent = onUiIntent,
            modifier = modifier,
        )
    }
}
