package com.paranid5.crescendo.playing.presentation.views.title_author

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

@Composable
internal fun TitleAndAuthor(
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier,
    textAlignment: Alignment.Horizontal = Alignment.Start,
) {
    val paletteColor = palette.getBrightDominantOrPrimary()

    Column(modifier) {
        Title(
            audioStatus = audioStatus,
            paletteColor = paletteColor,
            modifier = Modifier.align(textAlignment)
        )

        Spacer(Modifier.height(8.dp))

        Author(
            audioStatus = audioStatus,
            paletteColor = paletteColor,
            modifier = Modifier.align(textAlignment)
        )
    }
}