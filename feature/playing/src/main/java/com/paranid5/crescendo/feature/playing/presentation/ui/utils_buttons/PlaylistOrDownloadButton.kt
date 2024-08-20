package com.paranid5.crescendo.feature.playing.presentation.ui.utils_buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

@Composable
internal fun PlaylistOrDownloadButton(
    state: PlayingState,
    modifier: Modifier = Modifier,
) = nullable {
    val tint = LocalPalette.current.getBrightDominantOrPrimary()

    when (state.screenPlaybackStatus.bind()) {
        PlaybackStatus.STREAMING -> DownloadButton(
            tint = tint,
            url = state.playingStreamUrl,
            isLiveStreaming = state.isLiveStreaming,
            modifier = modifier,
        )

        PlaybackStatus.PLAYING -> CurrentPlaylistButton(
            tint = tint,
            modifier = modifier,
        )
    }
}
