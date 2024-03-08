package com.paranid5.crescendo.presentation.main.playing.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.views.properties.PropertiesButton
import com.paranid5.crescendo.presentation.main.playing.views.title_author.TitleAndAuthor

@Composable
fun TitleAndPropertiesButton(
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier,
    textAlignment: Alignment.Horizontal = Alignment.Start
) = Row(modifier) {
    TitleAndAuthor(
        palette = palette,
        audioStatus = audioStatus,
        textAlignment = textAlignment,
        modifier = Modifier.weight(1F)
    )

    Spacer(Modifier.width(10.dp))

    PropertiesButton(
        palette = palette,
        audioStatus = audioStatus
    )
}