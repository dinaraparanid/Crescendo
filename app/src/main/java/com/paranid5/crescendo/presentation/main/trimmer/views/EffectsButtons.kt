package com.paranid5.crescendo.presentation.main.trimmer.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.main.trimmer.views.effects.FadeButton
import com.paranid5.crescendo.presentation.main.trimmer.views.effects.PitchSpeedButton

@Composable
fun EffectsButtons(
    shownEffectsState: MutableIntState,
    modifier: Modifier = Modifier
) = Row(modifier) {
    FadeButton(
        shownEffectsState = shownEffectsState,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(horizontal = 4.dp)
    )

    PitchSpeedButton(
        shownEffectsState = shownEffectsState,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(horizontal = 4.dp)
    )
}