package com.paranid5.crescendo.trimmer.presentation.ui.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_CIRCLE_CENTER
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_RECT_OFFSET
import com.paranid5.crescendo.trimmer.presentation.DEFAULT_GRAPHICS_LAYER_ALPHA
import com.paranid5.crescendo.trimmer.presentation.PLAYBACK_CIRCLE_CENTER
import com.paranid5.crescendo.trimmer.presentation.PLAYBACK_CIRCLE_RADIUS
import com.paranid5.crescendo.trimmer.presentation.PLAYBACK_RECT_OFFSET
import com.paranid5.crescendo.trimmer.presentation.PLAYBACK_RECT_WIDTH

@Composable
internal fun PlaybackPosition(modifier: Modifier = Modifier) {
    val color = colors.background.alternative
    val playbackBrush = remember(color) { SolidColor(color) }

    Canvas(modifier.graphicsLayer(alpha = DEFAULT_GRAPHICS_LAYER_ALPHA)) {
        drawUpperTouchPad(playbackBrush)
        drawPlaybackPositionController(playbackBrush)
        drawLowerTouchPad(playbackBrush)
    }
}

internal fun PlaybackPositionOffset(playbackOffset: Float, waveformWidth: Int) =
    (CONTROLLER_CIRCLE_CENTER / 2 +
            playbackOffset * (waveformWidth - CONTROLLER_CIRCLE_RADIUS - CONTROLLER_RECT_OFFSET) +
            CONTROLLER_RECT_OFFSET).toInt()

private fun DrawScope.drawUpperTouchPad(brush: SolidColor) =
    drawCircle(
        brush = brush,
        radius = PLAYBACK_CIRCLE_RADIUS,
        center = Offset(PLAYBACK_CIRCLE_CENTER, 0F),
    )

private fun DrawScope.drawPlaybackPositionController(brush: SolidColor) =
    drawRoundRect(
        brush = brush,
        topLeft = Offset(PLAYBACK_CIRCLE_CENTER - PLAYBACK_RECT_OFFSET, 0F),
        size = Size(
            width = PLAYBACK_RECT_WIDTH,
            height = size.height - CONTROLLER_HEIGHT_OFFSET
        ),
        cornerRadius = CornerRadius(2F, 2F),
        blendMode = BlendMode.SrcAtop,
    )

private fun DrawScope.drawLowerTouchPad(brush: SolidColor) =
    drawCircle(
        brush = brush,
        radius = PLAYBACK_CIRCLE_RADIUS,
        center = Offset(PLAYBACK_CIRCLE_CENTER, size.height - CONTROLLER_HEIGHT_OFFSET),
    )