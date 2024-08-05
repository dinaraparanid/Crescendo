package com.paranid5.crescendo.playing.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.playing.view_model.PlayingBackResult
import com.paranid5.crescendo.playing.view_model.PlayingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SubscribeOnBackResultEffect(
    viewModel: PlayingViewModel = koinViewModel(),
    onBack: (PlayingBackResult) -> Unit,
) {
    val backResult by viewModel.backResultState.collectAsState()

    LaunchedEffect(backResult, onBack) {
        backResult?.let(onBack)
    }
}