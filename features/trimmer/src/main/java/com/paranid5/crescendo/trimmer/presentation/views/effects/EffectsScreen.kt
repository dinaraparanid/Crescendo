package com.paranid5.crescendo.trimmer.presentation.views.effects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.trimmer.domain.entities.ShownEffects

@Composable
internal fun EffectsScreen(
    effects: ShownEffects,
    modifier: Modifier = Modifier
) = when (effects) {
    ShownEffects.NONE -> Unit
    ShownEffects.FADE -> FadeScreen(modifier)
    ShownEffects.PITCH_SPEED -> PitchSpeedScreen(modifier)
}