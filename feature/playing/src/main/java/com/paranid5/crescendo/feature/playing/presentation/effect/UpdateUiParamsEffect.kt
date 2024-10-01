package com.paranid5.crescendo.feature.playing.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent

@Composable
internal fun UpdateUiParamsEffect(
    screenPlaybackStatus: PlaybackStatus,
    onUiIntent: (PlayingUiIntent) -> Unit,
) = LaunchedEffect(screenPlaybackStatus, onUiIntent) {
    onUiIntent(
        PlayingUiIntent.UpdateState.UpdateUiParams(
            visiblePlaybackStatus = screenPlaybackStatus,
        )
    )
}
