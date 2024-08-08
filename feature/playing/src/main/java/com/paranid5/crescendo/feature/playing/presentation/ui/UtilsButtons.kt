package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.presentation.ui.utils_buttons.AudioEffectsButton
import com.paranid5.crescendo.feature.playing.presentation.ui.utils_buttons.LikeButton
import com.paranid5.crescendo.feature.playing.presentation.ui.utils_buttons.PlaylistOrDownloadButton
import com.paranid5.crescendo.feature.playing.presentation.ui.utils_buttons.RepeatButton
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

@Composable
internal fun UtilsButtons(
    state: PlayingState,
    onUiIntent: (PlayingUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Row(modifier) {
    val tint = LocalPalette.current.getBrightDominantOrPrimary()

    AudioEffectsButton(
        tint = tint,
        modifier = Modifier.weight(1F),
    ) {
        onUiIntent(PlayingUiIntent.ScreenEffect.ShowAudioEffects)
    }

    RepeatButton(
        isRepeating = state.isRepeating,
        tint = tint,
        enabled = state.isScreenAudioStatusActual,
        modifier = Modifier.weight(1F),
    ) {
        onUiIntent(PlayingUiIntent.Playback.RepeatClick)
    }

    LikeButton(
        isLiked = state.isLiked,
        tint = tint,
        modifier = Modifier.weight(1F),
    ) {
        onUiIntent(PlayingUiIntent.UpdateState.LikeClick)
    }

    PlaylistOrDownloadButton(
        state = state,
        modifier = Modifier.weight(1F),
    )
}