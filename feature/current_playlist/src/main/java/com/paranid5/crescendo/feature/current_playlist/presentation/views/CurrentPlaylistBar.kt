package com.paranid5.crescendo.feature.current_playlist.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistState
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val CardLabelElevation = 4.dp

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
) = BarCardLabel(
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
) = BarCardLabel(
    modifier = modifier,
    text = stringResource(
        R.string.list_top_bar_duration,
        state.playlistState.playlistDurationFormatted,
    ),
)

@Composable
private fun BarCardLabel(
    text: String,
    modifier: Modifier = Modifier,
) = Box(
    modifier = modifier
        .clip(RoundedCornerShape(dimensions.padding.extraMedium))
        .background(colors.background.chips)
        .simpleShadow(elevation = CardLabelElevation)
) {
    Text(
        text = text,
        color = colors.text.primary,
        style = typography.body,
        modifier = Modifier
            .align(Alignment.Center)
            .padding(dimensions.padding.small),
    )
}
