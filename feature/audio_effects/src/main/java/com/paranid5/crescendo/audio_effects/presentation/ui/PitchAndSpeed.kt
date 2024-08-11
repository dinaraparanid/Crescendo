package com.paranid5.crescendo.audio_effects.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.audio_effects.presentation.ui.pitch_speed.AudioEffectEditor
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsState
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions

@Composable
internal fun PitchAndSpeed(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    val editorModifier = Modifier
        .fillMaxWidth()
        .weight(1F)

    PitchEditor(
        state = state,
        onUiIntent = onUiIntent,
        modifier = editorModifier,
    )

    Spacer(Modifier.height(dimensions.padding.extraMedium))

    SpeedEditor(
        state = state,
        onUiIntent = onUiIntent,
        modifier = editorModifier,
    )
}

@Composable
private fun PitchEditor(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = AudioEffectEditor(
    text = state.pitchText,
    modifier = modifier,
    effectTitle = stringResource(R.string.pitch),
    onValueChanged = { newPitch ->
        onUiIntent(AudioEffectsUiIntent.UpdateData.UpdatePitch(pitch = newPitch))
    },
)

@Composable
private fun SpeedEditor(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = AudioEffectEditor(
    text = state.speedText,
    modifier = modifier,
    effectTitle = stringResource(R.string.speed),
    onValueChanged = { newSpeed ->
        onUiIntent(AudioEffectsUiIntent.UpdateData.UpdateSpeed(speed = newSpeed))
    }
)
