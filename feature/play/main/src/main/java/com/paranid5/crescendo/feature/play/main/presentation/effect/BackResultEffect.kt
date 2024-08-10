package com.paranid5.crescendo.feature.play.main.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.feature.play.main.view_model.PlayBackResult
import com.paranid5.crescendo.feature.play.main.view_model.PlayState

@Composable
internal fun BackResultEffect(
    state: PlayState,
    onBack: (PlayBackResult) -> Unit,
    onHandled: () -> Unit,
) = LaunchedEffect(state.backResult, onBack, onHandled) {
    state.backResult?.let(onBack)
    onHandled()
}
