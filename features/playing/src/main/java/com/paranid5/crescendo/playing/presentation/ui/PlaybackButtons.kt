package com.paranid5.crescendo.playing.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.playing.presentation.rememberIsLiveStreaming
import com.paranid5.crescendo.playing.presentation.ui.playback_buttons.NextButton
import com.paranid5.crescendo.playing.presentation.ui.playback_buttons.PlayPauseButton
import com.paranid5.crescendo.playing.presentation.ui.playback_buttons.PrevButton

@Composable
internal fun PlaybackButtons(
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier,
) {
    val isLiveStreaming by rememberIsLiveStreaming(audioStatus)

    Row(modifier) {
        PrevButton(
            enabled = !isLiveStreaming,
            palette = palette,
            audioStatus = audioStatus,
            modifier = Modifier
                .weight(1F)
                .align(Alignment.CenterVertically)
        )

        PlayPauseButton(
            palette = palette,
            audioStatus = audioStatus,
            modifier = Modifier.size(54.dp).align(Alignment.CenterVertically)
        )

        NextButton(
            enabled = !isLiveStreaming,
            palette = palette,
            audioStatus = audioStatus,
            modifier = Modifier
                .weight(1F)
                .align(Alignment.CenterVertically)
        )
    }
}