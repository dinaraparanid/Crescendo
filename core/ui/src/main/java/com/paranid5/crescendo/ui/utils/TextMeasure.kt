package com.paranid5.crescendo.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import com.paranid5.crescendo.utils.extensions.pxToDp

@Composable
fun textWidth(text: String, style: TextStyle): Float {
    val playbackTextMeasurer = rememberTextMeasurer()

    val playbackPositionMeasure by remember(text, style) {
        derivedStateOf { playbackTextMeasurer.measure(text, style) }
    }

    val playbackTextWidthPx by remember(playbackPositionMeasure) {
        derivedStateOf { playbackPositionMeasure.size.width }
    }

    return playbackTextWidthPx.pxToDp().value
}