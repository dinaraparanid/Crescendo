package com.paranid5.crescendo.feature.playing.presentation.ui.title_author

import androidx.compose.foundation.basicMarquee
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.utils.extensions.artistAlbum

@Composable
internal fun Author(
    screenPlaybackStatus: PlaybackStatus,
    textColor: Color,
    state: PlayingState,
    modifier: Modifier = Modifier,
) {
    val author by rememberAuthor(
        screenPlaybackStatus = screenPlaybackStatus,
        state = state,
    )

    Text(
        text = author,
        maxLines = 1,
        color = textColor,
        style = typography.h.h4,
        modifier = modifier.basicMarquee(iterations = Int.MAX_VALUE),
    )
}

@Composable
private fun rememberAuthor(
    screenPlaybackStatus: PlaybackStatus,
    state: PlayingState,
): State<String> {
    val context = LocalContext.current

    val currentMetadata = state.currentMetadata
    val currentTrack = state.currentTrack

    return remember(context, screenPlaybackStatus, currentMetadata?.author, currentTrack?.artistAlbum) {
        val unknownChannel = context.getString(R.string.unknown_streamer)
        val unknownArtist = context.getString(R.string.unknown_artist)

        derivedStateOf {
            when (screenPlaybackStatus) {
                PlaybackStatus.STREAMING -> currentMetadata?.author ?: unknownChannel
                PlaybackStatus.PLAYING -> currentTrack?.artistAlbum ?: unknownArtist
            }
        }
    }
}
