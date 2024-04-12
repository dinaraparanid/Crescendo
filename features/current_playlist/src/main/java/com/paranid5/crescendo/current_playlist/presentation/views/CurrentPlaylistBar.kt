package com.paranid5.crescendo.current_playlist.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.current_playlist.presentation.CurrentPlaylistViewModel
import com.paranid5.crescendo.current_playlist.presentation.properties.compose.collectCurrentPlaylistDurationStrAsState
import com.paranid5.crescendo.current_playlist.presentation.properties.compose.collectCurrentPlaylistSizeAsState
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun CurrentPlaylistBar(modifier: Modifier = Modifier) =
    Row(modifier) {
        TracksLabel(Modifier.weight(1F))
        CurrentPlaylistDuration(Modifier.weight(1F))
    }

@Composable
private fun TracksLabel(
    modifier: Modifier = Modifier,
    viewModel: CurrentPlaylistViewModel = koinViewModel(),
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
    viewModel: CurrentPlaylistViewModel = koinViewModel(),
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