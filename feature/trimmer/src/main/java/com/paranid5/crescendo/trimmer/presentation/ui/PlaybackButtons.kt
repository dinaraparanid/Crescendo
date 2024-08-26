package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.presentation.ui.playback.PlayPauseButton
import com.paranid5.crescendo.trimmer.presentation.ui.playback.TenSecsBackButton
import com.paranid5.crescendo.trimmer.presentation.ui.playback.TenSecsForwardButton
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

private val ButtonSize = 48.dp

@Composable
internal fun PlaybackButtons(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Row(modifier) {
    TenSecsBackButton(
        onUiIntent = onUiIntent,
        modifier = Modifier
            .size(ButtonSize)
            .padding(dimensions.padding.extraSmall)
            .align(Alignment.CenterVertically),
    )

    Spacer(Modifier.width(dimensions.padding.extraMedium))

    PlayPauseButton(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .size(ButtonSize)
            .padding(dimensions.padding.extraSmall)
            .align(Alignment.CenterVertically),
    )

    Spacer(Modifier.width(dimensions.padding.extraMedium))

    TenSecsForwardButton(
        onUiIntent = onUiIntent,
        modifier = Modifier
            .size(ButtonSize)
            .padding(dimensions.padding.extraSmall)
            .align(Alignment.CenterVertically),
    )
}
