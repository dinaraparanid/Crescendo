package com.paranid5.crescendo.presentation.main.audio_effects.view.bass_reverb

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@Composable
fun AudioControllerWithLabel(
    value: Float,
    contentDescription: String,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0F..1F,
    angleRange: ClosedFloatingPointRange<Float> = -135F..135F
) {
    val colors = LocalAppColors.current

    Column(modifier) {
        AudioController(
            value = value,
            contentDescription = contentDescription,
            valueRange = valueRange,
            angleRange = angleRange,
            onValueChange = onValueChange,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(100.dp)
        )

        Text(
            text = contentDescription,
            color = colors.primary,
            fontSize = 10.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}