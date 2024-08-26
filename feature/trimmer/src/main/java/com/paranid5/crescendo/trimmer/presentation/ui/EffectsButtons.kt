package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.presentation.ui.effects.FadeButton
import com.paranid5.crescendo.trimmer.presentation.ui.effects.PitchSpeedButton
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

@Composable
internal fun EffectsButtons(
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Row(modifier) {
    FadeButton(
        onUiIntent = onUiIntent,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(horizontal = dimensions.padding.extraSmall),
    )

    PitchSpeedButton(
        onUiIntent = onUiIntent,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(horizontal = dimensions.padding.extraSmall),
    )
}
