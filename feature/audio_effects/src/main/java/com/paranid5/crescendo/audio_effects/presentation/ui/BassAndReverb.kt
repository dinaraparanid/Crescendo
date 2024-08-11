package com.paranid5.crescendo.audio_effects.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.audio_effects.presentation.ui.bass_reverb.AudioControllerWithLabel
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsState
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent
import com.paranid5.crescendo.core.media.eq.PresetReverb
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData.Companion.MILLIBELS_IN_DECIBEL

@Composable
internal fun BassAndReverb(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Row(modifier) {
    BassController(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.weight(1F),
    )

    Spacer(Modifier.width(dimensions.padding.big))

    ReverbController(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.weight(1F),
    )
}

@Composable
private fun BassController(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = AudioControllerWithLabel(
    value = state.bassStrength.toFloat(),
    contentDescription = stringResource(R.string.audio_effects_bass),
    valueRange = 0F..MILLIBELS_IN_DECIBEL.toFloat(),
    modifier = modifier,
    onValueChange = { bass ->
        val bassStrength = bass.toInt().toShort()
        onUiIntent(AudioEffectsUiIntent.UpdateData.UpdateBassStrength(bassStrength))
    }
)

@Composable
private fun ReverbController(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = AudioControllerWithLabel(
    value = state.reverbPreset.toFloat(),
    contentDescription = stringResource(R.string.audio_effects_reverb),
    valueRange = 0F..PresetReverb.presetsNumber.toFloat(),
    modifier = modifier,
    onValueChange = { reverb ->
        val reverbPreset = reverb.toInt().toShort()
        onUiIntent(AudioEffectsUiIntent.UpdateData.UpdateReverbPreset(reverbPreset))
    }
)
