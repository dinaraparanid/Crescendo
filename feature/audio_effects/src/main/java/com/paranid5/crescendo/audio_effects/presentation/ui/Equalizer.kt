package com.paranid5.crescendo.audio_effects.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.audio_effects.presentation.ui.equalizer.BandsWithCurve
import com.paranid5.crescendo.audio_effects.presentation.ui.equalizer.PresetSpinner
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsState
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions

@Composable
internal fun Equalizer(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    PresetSpinner(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.extraMedium),
    )

    Spacer(Modifier.height(dimensions.padding.medium))

    BandsWithCurve(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.fillMaxWidth(),
    )
}
