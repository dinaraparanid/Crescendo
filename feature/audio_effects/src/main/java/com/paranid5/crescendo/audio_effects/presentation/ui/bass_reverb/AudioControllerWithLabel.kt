package com.paranid5.crescendo.audio_effects.presentation.ui.bass_reverb

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppText

private val AudioControllerWidth = 100.dp
internal const val ControllerMinAngle = -135F
internal const val ControllerMaxAngle = 135F

@Composable
internal fun AudioControllerWithLabel(
    value: Float,
    contentDescription: String,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0F..1F,
    angleRange: ClosedFloatingPointRange<Float> = ControllerMinAngle..ControllerMaxAngle,
) = Column(modifier) {
    AudioController(
        value = value,
        contentDescription = contentDescription,
        valueRange = valueRange,
        angleRange = angleRange,
        onValueChange = onValueChange,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .width(AudioControllerWidth),
    )

    AppText(
        text = contentDescription,
        maxLines = 1,
        modifier = Modifier.align(Alignment.CenterHorizontally),
        style = typography.captionSm.copy(
            color = colors.text.primary,
            textAlign = TextAlign.Center,
        ),
    )
}
