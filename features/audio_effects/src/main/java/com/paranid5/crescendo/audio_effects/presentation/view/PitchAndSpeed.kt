package com.paranid5.crescendo.audio_effects.presentation.view

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
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import com.paranid5.crescendo.audio_effects.presentation.properties.compose.collectPitchTextAsNullableState
import com.paranid5.crescendo.audio_effects.presentation.properties.compose.collectSpeedTextAsNullableState
import com.paranid5.crescendo.audio_effects.presentation.view.pitch_speed.AudioEffectEditor
import com.paranid5.crescendo.core.resources.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PitchAndSpeed(modifier: Modifier = Modifier) =
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
    viewModel: AudioEffectsViewModel = koinViewModel(),
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
    viewModel: AudioEffectsViewModel = koinViewModel(),
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