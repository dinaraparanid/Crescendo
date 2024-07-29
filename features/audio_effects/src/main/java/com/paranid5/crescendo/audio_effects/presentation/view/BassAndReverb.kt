package com.paranid5.crescendo.audio_effects.presentation.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import com.paranid5.crescendo.audio_effects.presentation.properties.compose.collectBassStrengthAsNullableState
import com.paranid5.crescendo.audio_effects.presentation.properties.compose.collectReverbPresetAsNullableState
import com.paranid5.crescendo.audio_effects.presentation.view.bass_reverb.AudioControllerWithLabel
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.media.eq.PresetReverb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BassAndReverb(modifier: Modifier = Modifier) =
    Row(modifier) {
        BassController(Modifier.weight(1F))
        Spacer(Modifier.width(20.dp))
        ReverbController(Modifier.weight(1F))
    }

@Composable
private fun BassController(
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinViewModel(),
) {
    val bassValue by rememberBassValue()

    if (bassValue != null)
        AudioControllerWithLabel(
            value = bassValue!!,
            contentDescription = stringResource(R.string.bass),
            valueRange = 0F..1000F,
            modifier = modifier,
            onValueChange = { bass ->
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.updateBassStrength(bass.toInt().toShort())
                }
            }
        )
}

@Composable
private fun ReverbController(
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinViewModel(),
) {
    val reverbValue by rememberReverbValue()

    if (reverbValue != null)
        AudioControllerWithLabel(
            value = reverbValue!!,
            contentDescription = stringResource(R.string.reverb),
            valueRange = 0F..PresetReverb.presetsNumber.toFloat(),
            modifier = modifier,
            onValueChange = { reverb ->
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.updateReverbPreset(reverb.toInt().toShort())
                }
            }
        )
}

@Composable
private fun rememberBassValue(
    viewModel: AudioEffectsViewModel = koinViewModel(),
): State<Float?> {
    val bassStrength by viewModel.collectBassStrengthAsNullableState()

    return remember(bassStrength) {
        derivedStateOf { bassStrength?.toFloat() }
    }
}

@Composable
private fun rememberReverbValue(
    viewModel: AudioEffectsViewModel = koinViewModel(),
): State<Float?> {
    val reverbPreset by viewModel.collectReverbPresetAsNullableState()

    return remember(reverbPreset) {
        derivedStateOf { reverbPreset?.toFloat() }
    }
}