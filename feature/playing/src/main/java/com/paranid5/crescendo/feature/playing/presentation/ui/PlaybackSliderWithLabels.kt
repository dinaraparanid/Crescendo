package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.playing.presentation.ui.title_author.TitleAndAuthor
import com.paranid5.crescendo.feature.playing.view_model.PlayingState

@Composable
internal fun PlaybackSliderWithLabels(
    state: PlayingState,
    modifier: Modifier = Modifier,
    seekTo: (position: Long) -> Unit,
) = PlaybackSlider(
    state = state,
    seekTo = seekTo,
    modifier = modifier,
) { curPosition, videoLength, color ->
    if (state.isLiveStreaming.not()) TimeText(time = curPosition, color = color)

    TitleAndAuthor(
        state = state,
        textAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = dimensions.padding.medium)
            .weight(1F),
    )

    if (state.isLiveStreaming.not()) TimeText(time = videoLength, color = color)
}
