package com.paranid5.crescendo.audio_effects.presentation.ui.pitch_speed

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppText

@Composable
internal fun AudioEffectLabel(
    effectTitle: String,
    modifier: Modifier = Modifier,
) = AppText(
    text = effectTitle,
    modifier = modifier,
    style = typography.caption.copy(
        textAlign = TextAlign.Center,
        color = colors.text.primary,
    ),
)
