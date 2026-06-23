package com.paranid5.crescendo.feature.playing.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.feature.playing.view_model.PlayingScreenEffect
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun PlayingScreenEffect(
    effectFlow: SharedFlow<PlayingScreenEffect>,
    onScreenEffect: (PlayingScreenEffect) -> Unit,
) {
    LaunchedEffect(effectFlow, onScreenEffect) {
        effectFlow.collectLatest {
            onScreenEffect(it)
        }
    }
}
