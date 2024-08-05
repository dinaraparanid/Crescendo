package com.paranid5.crescendo.playing.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.playing.presentation.ui.utils_buttons.EqualizerButton
import com.paranid5.crescendo.playing.presentation.ui.utils_buttons.LikeButton
import com.paranid5.crescendo.playing.presentation.ui.utils_buttons.PlaylistOrDownloadButton
import com.paranid5.crescendo.playing.presentation.ui.utils_buttons.RepeatButton

@Composable
internal fun UtilsButtons(
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier
) = Row(modifier) {
    EqualizerButton(
        palette = palette,
        modifier = Modifier.weight(1F)
    )

    RepeatButton(
        palette = palette,
        modifier = Modifier.weight(1F)
    )

    LikeButton(
        palette = palette,
        modifier = Modifier.weight(1F)
    )

    PlaylistOrDownloadButton(
        palette = palette,
        audioStatus = audioStatus,
        modifier = Modifier.weight(1F)
    )
}