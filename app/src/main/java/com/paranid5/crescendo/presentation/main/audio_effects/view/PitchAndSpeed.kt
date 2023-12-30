package com.paranid5.crescendo.presentation.main.audio_effects.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.R
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.presentation.main.audio_effects.properties.compose.collectPitchTextAsNullableState
import com.paranid5.crescendo.presentation.main.audio_effects.properties.compose.collectSpeedTextAsNullableState
import com.paranid5.crescendo.presentation.main.audio_effects.view.pitch_speed.AudioEffectEditor
import kotlinx.coroutines.launch

@Composable
fun PitchAndSpeed(modifier: Modifier = Modifier) =
    Column(modifier) {
        PitchEditor(
            Modifier
                .fillMaxWidth()
                .weight(1F)
        )

        Spacer(Modifier.height(15.dp))

        SpeedEditor(
            Modifier
                .fillMaxWidth()
                .weight(1F)
        )
    }

@Composable
private fun PitchEditor(
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinActivityViewModel(),
) {
    val pitchText by viewModel.collectPitchTextAsNullableState()

    if (pitchText != null)
        AudioEffectEditor(
            text = pitchText,
            modifier = modifier,
            effectTitle = stringResource(R.string.pitch),
            onValueChanged = { newPitch ->
                viewModel.viewModelScope.launch {
                    viewModel.setPitch(newPitch)
                }
            }
        )
}

@Composable
private fun SpeedEditor(
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinActivityViewModel(),
) {
    val speedText by viewModel.collectSpeedTextAsNullableState()

    if (speedText != null)
        AudioEffectEditor(
            text = speedText,
            modifier = modifier,
            effectTitle = stringResource(R.string.speed),
            onValueChanged = { newSpeed ->
                viewModel.viewModelScope.launch {
                    viewModel.setSpeed(newSpeed)
                }
            }
        )
}