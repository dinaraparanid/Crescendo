package com.paranid5.crescendo.feature.playing.presentation.ui.playback_buttons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent

@Composable
internal fun PlayPauseButton(
    state: PlayingState,
    onUiIntent: (PlayingUiIntent) -> Unit,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val isPlaying by rememberIsPlaying(state = state)

    when {
        isPlaying -> PauseButton(
            tint = tint,
            modifier = modifier,
            onClick = { onUiIntent(PlayingUiIntent.Playback.PauseButtonClick) },
        )

        else -> PlayButton(
            tint = tint,
            modifier = modifier,
            onClick = { onUiIntent(PlayingUiIntent.Playback.PlayButtonClick) },
        )
    }
}

@Composable
private fun rememberIsPlaying(state: PlayingState): State<Boolean> {
    val isScreenAudioStatusActual = state.isScreenAudioStatusActual
    val isPlaying = state.isPlaying

    return remember(isPlaying, isScreenAudioStatusActual) {
        derivedStateOf { isPlaying && isScreenAudioStatusActual }
    }
}
