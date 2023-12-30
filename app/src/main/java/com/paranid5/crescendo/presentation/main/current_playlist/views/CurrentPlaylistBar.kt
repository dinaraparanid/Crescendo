package com.paranid5.crescendo.presentation.main.current_playlist.views

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.domain.utils.extensions.timeString
import com.paranid5.crescendo.domain.utils.extensions.totalDuration
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.properties.compose.collectCurrentPlaylistAsState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CurrentPlaylistBar(
    modifier: Modifier = Modifier,
    viewModel: CurrentPlaylistViewModel = koinActivityViewModel(),
) {
    val currentPlaylist by viewModel.collectCurrentPlaylistAsState()

    Row(modifier) {
        TracksLabel(
            currentPlaylist = currentPlaylist,
            modifier = Modifier.weight(1F)
        )

        CurrentPlaylistDuration(
            currentPlaylist = currentPlaylist,
            modifier = Modifier.weight(1F)
        )
    }
}

@Composable
private fun TracksLabel(
    currentPlaylist: ImmutableList<DefaultTrack>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val tracksNumber = currentPlaylist.size

    Text(
        text = "${stringResource(R.string.tracks)}: $tracksNumber",
        color = colors.primary,
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}

@Composable
private fun CurrentPlaylistDuration(
    currentPlaylist: ImmutableList<DefaultTrack>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val totalDurationText = currentPlaylist.totalDuration.timeString

    Text(
        text = "${stringResource(R.string.duration)}: $totalDurationText",
        color = colors.primary,
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}