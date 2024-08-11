package com.paranid5.crescendo.audio_effects.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsState
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

private const val TrackDecreasedBrightnessRatio = 0.5F
private const val BorderDecreasedBrightnessRatio = 0.25F

@Composable
internal fun TopBar(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    AudioEffectsLabel(modifier = Modifier.align(Alignment.Center))

    AudioEffectsSwitch(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.align(Alignment.CenterEnd),
    )
}

@Composable
private fun AudioEffectsLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.audio_effects_title),
        color = colors.text.primary,
        style = typography.h.h2.copy(shadow = Shadow(color = colors.primary)),
        modifier = modifier,
    )

@Composable
private fun AudioEffectsSwitch(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Switch(
    modifier = modifier,
    checked = state.areAudioEffectsEnabled,
    colors = SwitchDefaults.colors(
        checkedThumbColor = colors.background.card,
        checkedTrackColor = colors.background.highContrast,
        checkedBorderColor = colors.background.primary,
    ),
    onCheckedChange = {
        onUiIntent(AudioEffectsUiIntent.UpdateData.UpdateAudioEffectsEnabled(enabled = it))
    },
)
