package com.paranid5.crescendo.feature.current_playlist.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistState
import com.paranid5.crescendo.ui.foundation.AppBarCardLabel

@Composable
internal fun CurrentPlaylistBar(
    state: CurrentPlaylistState,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
) {
    TotalLabel(state = state)
    Spacer(Modifier.weight(1F))
    DurationLabel(state = state)
}

@Composable
private fun TotalLabel(
    state: CurrentPlaylistState,
    modifier: Modifier = Modifier,
) = AppBarCardLabel(
    modifier = modifier,
    text = stringResource(
        R.string.list_top_bar_total,
        state.playlistState.playlistSize,
    ),
)

@Composable
private fun DurationLabel(
    state: CurrentPlaylistState,
    modifier: Modifier = Modifier,
) = AppBarCardLabel(
    modifier = modifier,
    text = stringResource(
        R.string.list_top_bar_duration,
        state.playlistState.playlistDurationFormatted,
    ),
)
