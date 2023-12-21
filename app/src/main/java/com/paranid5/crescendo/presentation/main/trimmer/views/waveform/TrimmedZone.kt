package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_HEIGHT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.DEFAULT_GRAPHICS_LAYER_ALPHA
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.endOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.startOffsetFlow
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun TrimmedZone(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val progressBrush = SolidColor(colors.primary.copy(alpha = 0.25F))

    val startOffset by viewModel.startOffsetFlow.collectAsState(initial = 0F)
    val endOffset by viewModel.endOffsetFlow.collectAsState(initial = 0F)

    Canvas(modifier.graphicsLayer(alpha = DEFAULT_GRAPHICS_LAYER_ALPHA)) {
        drawRect(
            brush = progressBrush,
            topLeft = Offset(startOffset * size.width, 0F),
            size = Size(
                width = (endOffset - startOffset) * size.width,
                height = size.height - CONTROLLER_HEIGHT_OFFSET
            )
        )
    }
}