package com.paranid5.crescendo.audio_effects.presentation.view.pitch_speed

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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

internal const val MAX_INPUT_LENGTH = 4

@Composable
internal fun AudioEffectEditor(
    text: String?,
    effectTitle: String,
    onValueChanged: (effectVal: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val effectInputState = remember {
        mutableStateOf(text ?: "")
    }

    val effectInput by effectInputState

    val effectValState = remember {
        mutableFloatStateOf(effectInput.toFloatOrNull() ?: 1F)
    }

    val textFieldText by remember(effectInput) {
        derivedStateOf { effectInput.take(MAX_INPUT_LENGTH) }
    }

    Row(modifier) {
        AudioEffectLabel(
            effectTitle = effectTitle,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(50.dp)
                .padding(end = 5.dp)
        )

        AudioEffectBand(
            effectTitle = effectTitle,
            effectValState = effectValState,
            effectInputState = effectInputState,
            onValueChanged = onValueChanged,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1F)
        )

        AudioEffectTextField(
            value = textFieldText,
            effectInputState = effectInputState,
            effectValState = effectValState,
            onValueChanged = onValueChanged,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(50.dp)
                .padding(start = 10.dp)
        )
    }
}