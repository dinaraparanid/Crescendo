package com.paranid5.crescendo.presentation.main.audio_effects.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.utils.extensions.PresetReverb
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsUIHandler
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.presentation.main.audio_effects.view.bass_reverb.AudioControllerWithLabel
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun BassAndReverb(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = Row(modifier) {
    BassController(
        viewModel = viewModel,
        modifier = Modifier.weight(1F)
    )

    Spacer(Modifier.width(20.dp))

    ReverbController(
        viewModel = viewModel,
        modifier = Modifier.weight(1F)
    )
}

@Composable
private fun BassController(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current

    val bassValue by rememberBassValue(viewModel)

    val audioStatus by viewModel
        .audioStatusFlow
        .collectLatestAsState(initial = null)

    if (bassValue != null)
        AudioControllerWithLabel(
            value = bassValue!!,
            contentDescription = stringResource(R.string.bass),
            valueRange = 0F..1000F,
            modifier = modifier
        ) { bass ->
            viewModel.viewModelScope.launch {
                audioEffectsUIHandler.storeAndSendBassStrength(
                    context = context,
                    bassStrength = bass.toInt().toShort(),
                    audioStatus = audioStatus!!
                )
            }
        }
}

@Composable
private fun ReverbController(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current

    val reverbValue by rememberReverbValue(viewModel)

    val audioStatus by viewModel
        .audioStatusFlow
        .collectLatestAsState(initial = null)

    if (reverbValue != null)
        AudioControllerWithLabel(
            value = reverbValue!!,
            contentDescription = stringResource(R.string.reverb),
            valueRange = 0F..PresetReverb.presetsNumber.toFloat(),
            modifier = modifier
        ) { reverb ->
            viewModel.viewModelScope.launch {
                audioEffectsUIHandler.storeAndSendReverbPresetAsync(
                    context = context,
                    reverbPreset = reverb.toInt().toShort(),
                    audioStatus = audioStatus!!
                )
            }
        }
}

@Composable
private fun rememberBassValue(viewModel: AudioEffectsViewModel): State<Float?> {
    val bassStrength by viewModel
        .bassStrengthFlow
        .collectLatestAsState(initial = null)

    return remember(bassStrength) {
        derivedStateOf { bassStrength?.toFloat() }
    }
}

@Composable
private fun rememberReverbValue(viewModel: AudioEffectsViewModel): State<Float?> {
    val reverbPreset by viewModel
        .reverbPresetFlow
        .collectLatestAsState(initial = null)

    return remember(reverbPreset) {
        derivedStateOf { reverbPreset?.toFloat() }
    }
}