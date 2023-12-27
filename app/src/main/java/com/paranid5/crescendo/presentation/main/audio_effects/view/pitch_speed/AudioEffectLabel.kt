package com.paranid5.crescendo.presentation.main.audio_effects.view.pitch_speed

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
internal fun AudioEffectLabel(
    effectTitle: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Text(
        text = effectTitle,
        textAlign = TextAlign.Center,
        color = colors.primary,
        fontSize = 12.sp,
        maxLines = 1,
        modifier = modifier
    )
}