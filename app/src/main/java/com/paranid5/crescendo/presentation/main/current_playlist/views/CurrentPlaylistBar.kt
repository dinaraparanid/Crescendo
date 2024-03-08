package com.paranid5.crescendo.presentation.main.current_playlist.views

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.properties.compose.collectCurrentPlaylistDurationStrAsState
import com.paranid5.crescendo.presentation.main.current_playlist.properties.compose.collectCurrentPlaylistSizeAsState
import com.paranid5.crescendo.presentation.ui.LocalAppColors

@Composable
fun CurrentPlaylistBar(modifier: Modifier = Modifier) =
    Row(modifier) {
        TracksLabel(Modifier.weight(1F))
        CurrentPlaylistDuration(Modifier.weight(1F))
    }

@Composable
private fun TracksLabel(
    modifier: Modifier = Modifier,
    viewModel: CurrentPlaylistViewModel = koinActivityViewModel(),
) {
    val colors = LocalAppColors.current
    val tracksNumber by viewModel.collectCurrentPlaylistSizeAsState()

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
    modifier: Modifier = Modifier,
    viewModel: CurrentPlaylistViewModel = koinActivityViewModel(),
) {
    val colors = LocalAppColors.current
    val totalDurationText by viewModel.collectCurrentPlaylistDurationStrAsState()

    Text(
        text = "${stringResource(R.string.duration)}: $totalDurationText",
        color = colors.primary,
        fontSize = 14.sp,
        textAlign = TextAlign.End,
        modifier = modifier
    )
}