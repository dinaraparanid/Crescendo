package com.paranid5.crescendo.audio_effects.presentation.ui.equalizer

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors

private val BandsCurveStrokeWidth = 3.dp

@Composable
internal fun BandsCurve(
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier,
) {
    val appColors = colors
    val path = BandsPath(pointsState)

    Canvas(modifier) {
        drawBandsCurve(
            path = path,
            color = appColors.primary,
        )
    }
}

private fun BandsPath(pointsState: SnapshotStateList<Offset>) =
    Path().apply {
        if (pointsState.isNotEmpty()) {
            moveTo(pointsState[0].x, pointsState[0].y)

            (1 until pointsState.size).forEach { i ->
                cubicToPoint(
                    prevPoint = pointsState[i - 1],
                    nextPoint = pointsState[i]
                )
            }
        }
    }

@Suppress("UnnecessaryVariable")
private fun Path.cubicToPoint(
    prevPoint: Offset,
    nextPoint: Offset,
) {
    val (prevX, prevY) = prevPoint
    val (nextX, nextY) = nextPoint

    val conX1 = (prevX + nextX) / 2F
    val conX2 = (prevX + nextX) / 2F

    val conY1 = prevY
    val conY2 = nextY

    cubicTo(
        x1 = conX1,
        y1 = conY1,
        x2 = conX2,
        y2 = conY2,
        x3 = nextX,
        y3 = nextY,
    )
}

private fun DrawScope.drawBandsCurve(path: Path, color: Color) =
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = BandsCurveStrokeWidth.toPx(),
            cap = StrokeCap.Round,
        ),
    )
