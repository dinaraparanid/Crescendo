package com.paranid5.crescendo.playing.presentation.views.title_author

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
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.playing.presentation.PlayingViewModel
import com.paranid5.crescendo.playing.presentation.properties.compose.collectCurrentMetadataAsState
import com.paranid5.crescendo.playing.presentation.properties.compose.collectCurrentTrackAsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Title(
    audioStatus: AudioStatus,
    paletteColor: Color,
    modifier: Modifier = Modifier
) {
    val title by rememberTitle(audioStatus)

    Text(
        text = title,
        fontSize = 24.sp,
        maxLines = 1,
        color = paletteColor,
        modifier = modifier.basicMarquee(iterations = Int.MAX_VALUE)
    )
}

@Composable
private fun rememberTitle(
    audioStatus: AudioStatus,
    viewModel: PlayingViewModel = koinViewModel(),
): State<String> {
    val currentMetadata by viewModel.collectCurrentMetadataAsState()
    val currentTrack by viewModel.collectCurrentTrackAsState()

    val unknownStream = stringResource(R.string.stream_no_name)
    val unknownTrack = stringResource(R.string.unknown_track)

    return remember(audioStatus, currentMetadata?.title, currentTrack?.title) {
        derivedStateOf {
            when (audioStatus) {
                AudioStatus.STREAMING -> currentMetadata?.title ?: unknownStream
                AudioStatus.PLAYING -> currentTrack?.title ?: unknownTrack
            }
        }
    }
}