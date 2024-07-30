package com.paranid5.crescendo.audio_effects.presentation.view.pitch_speed

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
    color = colors.primary,
    style = typography.caption,
    maxLines = 1,
    modifier = modifier,
)