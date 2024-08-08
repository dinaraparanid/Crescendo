package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.presentation.ui.playback_buttons.NextButton
import com.paranid5.crescendo.feature.playing.presentation.ui.playback_buttons.PlayPauseButton
import com.paranid5.crescendo.feature.playing.presentation.ui.playback_buttons.PrevButton
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

private val PlayButtonSize = 54.dp

@Composable
internal fun PlaybackButtons(
    state: PlayingState,
    onUiIntent: (PlayingUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        PrevButton(
            enabled = state.isLiveStreaming.not(),
            tint = palette.getBrightDominantOrPrimary(),
            modifier = Modifier.weight(1F),
            onClick = { onUiIntent(PlayingUiIntent.Playback.PrevButtonClick) },
        )

        PlayPauseButton(
            state = state,
            onUiIntent = onUiIntent,
            tint = palette.getBrightDominantOrPrimary(),
            modifier = Modifier.size(PlayButtonSize),
        )

        NextButton(
            enabled = state.isLiveStreaming.not(),
            tint = palette.getBrightDominantOrPrimary(),
            modifier = Modifier.weight(1F),
            onClick = { onUiIntent(PlayingUiIntent.Playback.NextButtonClick) },
        )
    }
}
