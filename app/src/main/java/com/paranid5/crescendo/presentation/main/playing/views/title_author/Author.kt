package com.paranid5.crescendo.presentation.main.playing.views.title_author

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
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.domain.utils.extensions.artistAlbum
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectCurrentMetadataAsState
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectCurrentTrackAsState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Author(
    audioStatus: AudioStatus,
    paletteColor: Color,
    modifier: Modifier = Modifier
) {
    val author by rememberAuthor(audioStatus)

    Text(
        text = author,
        fontSize = 18.sp,
        maxLines = 1,
        color = paletteColor,
        modifier = modifier.basicMarquee(iterations = Int.MAX_VALUE)
    )
}

@Composable
private fun rememberAuthor(
    audioStatus: AudioStatus,
    viewModel: PlayingViewModel = koinActivityViewModel(),
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