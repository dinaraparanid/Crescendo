package com.paranid5.crescendo.tracks.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.tracks.view_model.TracksBackResult
import com.paranid5.crescendo.tracks.view_model.TracksState

@Composable
internal fun SubscribeOnBackEventsEffect(
    state: TracksState,
    onBack: (TracksBackResult) -> Unit,
) = LaunchedEffect(state, onBack) {
    state.backResult?.let(onBack)
}
