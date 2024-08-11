package com.paranid5.crescendo.audio_effects.presentation.ui.pitch_speed

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions

internal const val MaxInputLength = 4

private val TextsWidth = 50.dp

@Composable
internal fun AudioEffectEditor(
    text: String,
    effectTitle: String,
    onValueChanged: (effectVal: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val effectInputState = remember { mutableStateOf(text) }
    val effectInput by effectInputState

    val effectValState = remember {
        mutableFloatStateOf(effectInput.toFloatOrNull() ?: 1F)
    }

    val textFieldText by remember(effectInput) {
        derivedStateOf { effectInput.take(MaxInputLength) }
    }

    Row(modifier) {
        AudioEffectLabel(
            effectTitle = effectTitle,
            modifier = Modifier
                .width(TextsWidth)
                .align(Alignment.CenterVertically),
        )

        Spacer(Modifier.width(dimensions.padding.small))

        AudioEffectBand(
            effectTitle = effectTitle,
            effectValState = effectValState,
            effectInputState = effectInputState,
            onValueChanged = onValueChanged,
            modifier = Modifier
                .weight(1F)
                .align(Alignment.CenterVertically),
        )

        Spacer(Modifier.width(dimensions.padding.medium))

        AudioEffectTextField(
            value = textFieldText,
            effectInputState = effectInputState,
            effectValState = effectValState,
            onValueChanged = onValueChanged,
            modifier = Modifier
                .width(TextsWidth)
                .align(Alignment.CenterVertically),
        )
    }
}
