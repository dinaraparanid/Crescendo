package com.paranid5.crescendo.presentation.main.trimmer.views.effects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.impl.trimming.ShownEffects

@Composable
fun EffectsScreen(
    effects: ShownEffects,
    modifier: Modifier = Modifier
) = when (effects) {
    ShownEffects.NONE -> Unit
    ShownEffects.FADE -> FadeScreen(modifier)
    ShownEffects.PITCH_SPEED -> PitchSpeedScreen(modifier)
}