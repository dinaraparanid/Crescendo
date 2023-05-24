package com.paranid5.mediastreamer.presentation.audio_effects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.mediastreamer.presentation.ui.rememberVideoCoverPainterWithPalette

@Composable
fun AudioEffectsScreen(modifier: Modifier = Modifier) {
    val (_, palette) = rememberVideoCoverPainterWithPalette(
        isPlaceholderRequired = false,
        size = 1100 to 1000
    )

    Column(modifier) {
        UpBar(palette = palette, modifier = Modifier.fillMaxWidth())
    }
}