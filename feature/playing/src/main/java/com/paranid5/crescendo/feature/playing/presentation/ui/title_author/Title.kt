package com.paranid5.crescendo.feature.playing.presentation.ui.title_author

import androidx.compose.foundation.basicMarquee
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
import com.paranid5.crescendo.ui.foundation.AppText

@Composable
internal fun Title(
    screenPlaybackStatus: PlaybackStatus,
    textColor: Color,
    state: PlayingState,
    modifier: Modifier = Modifier,
) {
    val title by rememberTitle(screenPlaybackStatus = screenPlaybackStatus, state = state)

    AppText(
        text = title,
        maxLines = 1,
        modifier = modifier.basicMarquee(iterations = Int.MAX_VALUE),
        style = typography.h.h2.copy(
            color = textColor,
        ),
    )
}

@Composable
private fun rememberTitle(
    state: PlayingState,
    screenPlaybackStatus: PlaybackStatus,
): State<String> {
    val context = LocalContext.current

    val currentMetadata = state.currentMetadata
    val currentTrack = state.currentTrack

    return remember(context, screenPlaybackStatus, currentMetadata?.title, currentTrack?.title) {
        val unknownStream = context.getString(R.string.stream_no_name)
        val unknownTrack = context.getString(R.string.unknown_track)

        derivedStateOf {
            when (screenPlaybackStatus) {
                PlaybackStatus.STREAMING -> currentMetadata?.title ?: unknownStream
                PlaybackStatus.PLAYING -> currentTrack?.title ?: unknownTrack
            }
        }
    }
}
