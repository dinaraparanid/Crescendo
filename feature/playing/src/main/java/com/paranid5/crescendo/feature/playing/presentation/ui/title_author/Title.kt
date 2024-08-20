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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Title(
    textColor: Color,
    state: PlayingState,
    modifier: Modifier = Modifier,
) {
    val title by rememberTitle(state)

    Text(
        text = title,
        maxLines = 1,
        color = textColor,
        style = typography.h.h2,
        modifier = modifier.basicMarquee(iterations = Int.MAX_VALUE),
    )
}

@Composable
private fun rememberTitle(state: PlayingState): State<String> {
    val audioStatus = state.screenPlaybackStatus
    val currentMetadata = state.currentMetadata
    val currentTrack = state.currentTrack

    val unknownStream = stringResource(R.string.stream_no_name)
    val unknownTrack = stringResource(R.string.unknown_track)

    return remember(audioStatus, currentMetadata?.title, currentTrack?.title) {
        derivedStateOf {
            when (audioStatus) {
                PlaybackStatus.STREAMING -> currentMetadata?.title ?: unknownStream
                PlaybackStatus.PLAYING -> currentTrack?.title ?: unknownTrack
                else -> ""
            }
        }
    }
}
