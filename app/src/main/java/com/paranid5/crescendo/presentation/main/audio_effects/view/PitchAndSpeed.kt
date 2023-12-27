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
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.presentation.main.audio_effects.view.pitch_speed.AudioEffectEditor
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import kotlinx.coroutines.launch

@Composable
fun PitchAndSpeed(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    PitchEditor(
        viewModel = viewModel,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
    )

    Spacer(Modifier.height(15.dp))

    SpeedEditor(
        viewModel = viewModel,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
    )
}

@Composable
private fun PitchEditor(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) {
    val pitchText by viewModel
        .pitchTextFlow
        .collectLatestAsState(initial = null)

    if (pitchText != null)
        AudioEffectEditor(
            text = pitchText,
            modifier = modifier,
            effectTitle = stringResource(R.string.pitch),
            onValueChanged = { newPitch ->
                viewModel.viewModelScope.launch {
                    viewModel.storePitch(newPitch)
                }
            }
        )
}

@Composable
private fun SpeedEditor(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) {
    val speedText by viewModel
        .speedTextState
        .collectLatestAsState(initial = null)

    if (speedText != null)
        AudioEffectEditor(
            text = speedText,
            modifier = modifier,
            effectTitle = stringResource(R.string.speed),
            onValueChanged = { newSpeed ->
                viewModel.viewModelScope.launch {
                    viewModel.storeSpeed(newSpeed)
                }
            }
        )
}