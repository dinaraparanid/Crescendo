package com.paranid5.crescendo.trimmer.presentation.ui.effects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.trimmer.domain.entities.ShownEffects
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.utils.doNothing

@Composable
internal fun EffectsScreen(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = when (state.shownEffects) {
    ShownEffects.NONE -> doNothing()

    ShownEffects.FADE -> FadeScreen(
        state = state,
        onUiIntent = onUiIntent,
        modifier = modifier,
    )

    ShownEffects.PITCH_SPEED -> PitchSpeedScreen(
        state = state,
        onUiIntent = onUiIntent,
        modifier = modifier,
    )
}
