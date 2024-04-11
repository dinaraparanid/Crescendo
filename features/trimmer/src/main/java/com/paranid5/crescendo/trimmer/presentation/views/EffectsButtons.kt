package com.paranid5.crescendo.trimmer.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.trimmer.presentation.views.effects.FadeButton
import com.paranid5.crescendo.trimmer.presentation.views.effects.PitchSpeedButton

@Composable
internal fun EffectsButtons(modifier: Modifier = Modifier) =
    Row(modifier) {
        FadeButton(
            Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 4.dp)
        )

        PitchSpeedButton(
            Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 4.dp)
        )
    }