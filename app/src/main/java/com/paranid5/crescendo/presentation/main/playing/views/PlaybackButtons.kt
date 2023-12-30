package com.paranid5.crescendo.presentation.main.playing.views

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.rememberIsLiveStreaming
import com.paranid5.crescendo.presentation.main.playing.views.playback_buttons.NextButton
import com.paranid5.crescendo.presentation.main.playing.views.playback_buttons.PlayPauseButton
import com.paranid5.crescendo.presentation.main.playing.views.playback_buttons.PrevButton

@Composable
fun PlaybackButtons(
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
            modifier = Modifier.weight(2F)
        )

        PlayPauseButton(
            palette = palette,
            audioStatus = audioStatus,
            modifier = Modifier.weight(1F)
        )

        NextButton(
            enabled = !isLiveStreaming,
            palette = palette,
            audioStatus = audioStatus,
            modifier = Modifier.weight(2F)
        )
    }
}