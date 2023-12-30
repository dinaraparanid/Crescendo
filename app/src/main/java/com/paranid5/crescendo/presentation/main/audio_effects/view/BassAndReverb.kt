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
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsUIHandler
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.presentation.main.audio_effects.properties.compose.collectAudioStatusAsState
import com.paranid5.crescendo.presentation.main.audio_effects.properties.compose.collectBassStrengthAsNullableState
import com.paranid5.crescendo.presentation.main.audio_effects.properties.compose.collectReverbPresetAsNullableState
import com.paranid5.crescendo.presentation.main.audio_effects.view.bass_reverb.AudioControllerWithLabel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun BassAndReverb(modifier: Modifier = Modifier) =
    Row(modifier) {
        BassController(Modifier.weight(1F))
        Spacer(Modifier.width(20.dp))
        ReverbController(Modifier.weight(1F))
    }

@Composable
private fun BassController(
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinActivityViewModel(),
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current

    val bassValue by rememberBassValue()
    val audioStatus by viewModel.collectAudioStatusAsState()

    if (bassValue != null)
        AudioControllerWithLabel(
            value = bassValue!!,
            contentDescription = stringResource(R.string.bass),
            valueRange = 0F..1000F,
            modifier = modifier,
            onValueChange = { bass ->
                viewModel.viewModelScope.launch {
                    audioEffectsUIHandler.storeAndSendBassStrength(
                        context = context,
                        bassStrength = bass.toInt().toShort(),
                        audioStatus = audioStatus!!
                    )
                }
            }
        )
}

@Composable
private fun ReverbController(
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinActivityViewModel(),
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current

    val reverbValue by rememberReverbValue()
    val audioStatus by viewModel.collectAudioStatusAsState()

    if (reverbValue != null)
        AudioControllerWithLabel(
            value = reverbValue!!,
            contentDescription = stringResource(R.string.reverb),
            valueRange = 0F..PresetReverb.presetsNumber.toFloat(),
            modifier = modifier,
            onValueChange = { reverb ->
                viewModel.viewModelScope.launch {
                    audioEffectsUIHandler.storeAndSendReverbPresetAsync(
                        context = context,
                        reverbPreset = reverb.toInt().toShort(),
                        audioStatus = audioStatus!!
                    )
                }
            }
        )
}

@Composable
private fun rememberBassValue(
    viewModel: AudioEffectsViewModel = koinActivityViewModel()
): State<Float?> {
    val bassStrength by viewModel.collectBassStrengthAsNullableState()

    return remember(bassStrength) {
        derivedStateOf { bassStrength?.toFloat() }
    }
}

@Composable
private fun rememberReverbValue(
    viewModel: AudioEffectsViewModel = koinActivityViewModel()
): State<Float?> {
    val reverbPreset by viewModel.collectReverbPresetAsNullableState()

    return remember(reverbPreset) {
        derivedStateOf { reverbPreset?.toFloat() }
    }
}