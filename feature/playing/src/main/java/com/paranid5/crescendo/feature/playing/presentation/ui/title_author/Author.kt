package com.paranid5.crescendo.feature.playing.presentation.ui.title_author

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.utils.extensions.artistAlbum

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Author(
    textColor: Color,
    state: PlayingState,
    modifier: Modifier = Modifier,
) {
    val author by rememberAuthor(state)

    Text(
        text = author,
        maxLines = 1,
        color = textColor,
        style = typography.h.h3,
        modifier = modifier.basicMarquee(iterations = Int.MAX_VALUE),
    )
}

@Composable
private fun rememberAuthor(state: PlayingState): State<String> {
    val audioStatus = state.screenPlaybackStatus
    val currentMetadata = state.currentMetadata
    val currentTrack = state.currentTrack

    val unknownChannel = stringResource(R.string.unknown_streamer)
    val unknownArtist = stringResource(R.string.unknown_artist)

    return remember(audioStatus, currentMetadata?.author, currentTrack?.artistAlbum) {
        derivedStateOf {
            when (audioStatus) {
                PlaybackStatus.STREAMING -> currentMetadata?.author ?: unknownChannel
                PlaybackStatus.PLAYING -> currentTrack?.artistAlbum ?: unknownArtist
                else -> ""
            }
        }
    }
}
