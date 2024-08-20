package com.paranid5.crescendo.feature.playing.presentation.ui.kebab

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent
import com.paranid5.crescendo.ui.track.item.TrackKebabMenuButton

private val IconSize = 24.dp

@Composable
internal fun KebabMenuButton(
    tint: Color,
    state: PlayingState,
    onUiIntent: (PlayingUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = nullable {
    when (state.screenPlaybackStatus.bind()) {
        PlaybackStatus.STREAMING -> VideoKebabMenuButton(
            tint = tint,
            modifier = modifier,
            iconModifier = Modifier.size(IconSize),
        )

        PlaybackStatus.PLAYING -> state.currentTrack?.let { currentTrack ->
            TrackKebabMenuButton(
                track = currentTrack,
                tint = tint,
                modifier = modifier,
                iconModifier = Modifier.size(IconSize),
                addToPlaylist = {
                    onUiIntent(PlayingUiIntent.UpdateState.AddTrackToPlaylist(track = it))
                },
                showTrimmer = {
                    onUiIntent(PlayingUiIntent.ScreenEffect.ShowTrimmer(trackUri = it))
                },
                showMetaEditor = {
                    onUiIntent(PlayingUiIntent.ScreenEffect.ShowMetaEditor)
                }
            )
        }
    }
}
