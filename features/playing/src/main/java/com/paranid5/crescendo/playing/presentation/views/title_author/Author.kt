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
import com.paranid5.crescendo.utils.extensions.artistAlbum
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Author(
    audioStatus: AudioStatus,
    paletteColor: Color,
    modifier: Modifier = Modifier
) {
    val author by rememberAuthor(audioStatus)

    Text(
        text = author,
        fontSize = 20.sp,
        maxLines = 1,
        color = paletteColor,
        modifier = modifier.basicMarquee(iterations = Int.MAX_VALUE)
    )
}

@Composable
private fun rememberAuthor(
    audioStatus: AudioStatus,
    viewModel: PlayingViewModel = koinViewModel(),
): State<String> {
    val currentMetadata by viewModel.collectCurrentMetadataAsState()
    val currentTrack by viewModel.collectCurrentTrackAsState()

    val unknownChannel = stringResource(R.string.unknown_streamer)
    val unknownArtist = stringResource(R.string.unknown_artist)

    return remember(audioStatus, currentMetadata?.author, currentTrack?.artistAlbum) {
        derivedStateOf {
            when (audioStatus) {
                AudioStatus.STREAMING -> currentMetadata?.author ?: unknownChannel
                AudioStatus.PLAYING -> currentTrack?.artistAlbum ?: unknownArtist
            }
        }
    }
}