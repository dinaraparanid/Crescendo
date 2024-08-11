package com.paranid5.crescendo.audio_effects.presentation.ui.pitch_speed

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@Composable
internal fun AudioEffectLabel(
    effectTitle: String,
    modifier: Modifier = Modifier
) = Text(
    text = effectTitle,
    textAlign = TextAlign.Center,
    color = colors.text.primary,
    style = typography.caption,
    modifier = modifier,
)
