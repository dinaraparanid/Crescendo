package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_CIRCLE_CENTER
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_RECT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.DEFAULT_GRAPHICS_LAYER_ALPHA
import com.paranid5.crescendo.presentation.main.trimmer.PLAYBACK_CIRCLE_CENTER
import com.paranid5.crescendo.presentation.main.trimmer.PLAYBACK_CIRCLE_RADIUS
import com.paranid5.crescendo.presentation.main.trimmer.PLAYBACK_RECT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.PLAYBACK_RECT_WIDTH
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun PlaybackPosition(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val playbackBrush = SolidColor(colors.backgroundAlternative)

    Canvas(modifier.graphicsLayer(alpha = DEFAULT_GRAPHICS_LAYER_ALPHA)) {
        drawUpperToucher(playbackBrush)
        drawPlaybackPositionController(playbackBrush)
        drawLowerToucher(playbackBrush)
    }
}

fun PlaybackPositionOffset(playbackOffset: Float, waveformWidth: Int) =
    (CONTROLLER_CIRCLE_CENTER / 2 +
            playbackOffset * (waveformWidth - CONTROLLER_CIRCLE_RADIUS - CONTROLLER_RECT_OFFSET) +
            CONTROLLER_RECT_OFFSET).toInt()

private fun DrawScope.drawUpperToucher(brush: SolidColor) =
    drawCircle(
        brush = brush,
        radius = PLAYBACK_CIRCLE_RADIUS,
        center = Offset(PLAYBACK_CIRCLE_CENTER, 0F)
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
        blendMode = BlendMode.SrcAtop
    )

private fun DrawScope.drawLowerToucher(brush: SolidColor) =
    drawCircle(
        brush = brush,
        radius = PLAYBACK_CIRCLE_RADIUS,
        center = Offset(PLAYBACK_CIRCLE_CENTER, size.height - CONTROLLER_HEIGHT_OFFSET)
    )