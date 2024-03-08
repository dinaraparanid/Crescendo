package com.paranid5.crescendo.presentation.main.playing.views.utils_buttons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.rememberIsLiveStreaming

@Composable
fun PlaylistOrDownloadButton(
    palette: Palette?,
    audioStatus: AudioStatus,
    modifier: Modifier = Modifier,
) {
    val isLiveStreaming by rememberIsLiveStreaming(audioStatus)

    when (audioStatus) {
        AudioStatus.STREAMING -> DownloadButton(
            palette = palette,
            isLiveStreaming = isLiveStreaming,
            modifier = modifier
        )

        AudioStatus.PLAYING -> CurrentPlaylistButton(
            palette = palette,
            modifier = modifier
        )
    }
}