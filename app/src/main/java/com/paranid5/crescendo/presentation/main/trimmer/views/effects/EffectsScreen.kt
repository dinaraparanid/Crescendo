package com.paranid5.crescendo.presentation.main.trimmer.views.effects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.presentation.main.trimmer.ShownEffects
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.views.effects.FadeScreen
import com.paranid5.crescendo.presentation.main.trimmer.views.effects.PitchSpeedScreen

@Composable
fun EffectsScreen(
    effects: ShownEffects,
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) = when (effects) {
    ShownEffects.NONE -> Unit
    ShownEffects.FADE -> FadeScreen(viewModel, modifier)
    ShownEffects.PITCH_SPEED -> PitchSpeedScreen(viewModel, modifier)
}