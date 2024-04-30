package com.paranid5.crescendo.playing.presentation.views

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.playing.presentation.views.title_author.TitleAndAuthor

@Composable
internal fun PlaybackSliderWithLabels(
    audioStatus: AudioStatus,
    durationMillis: Long,
    palette: Palette?,
    isLiveStreaming: Boolean,
    modifier: Modifier = Modifier,
) = PlaybackSlider(
    durationMillis = durationMillis,
    palette = palette,
    audioStatus = audioStatus,
    modifier = modifier
) { curPosition, videoLength, color ->
    if (isLiveStreaming) Unit else TimeText(curPosition, color)

    TitleAndAuthor(
        audioStatus = audioStatus,
        palette = palette,
        textAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .weight(1F, fill = true),
    )

    if (isLiveStreaming) Unit else TimeText(videoLength, color)
}