package com.paranid5.crescendo.audio_effects.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.audio_effects.presentation.view.equalizer.BandsWithCurve
import com.paranid5.crescendo.audio_effects.presentation.view.equalizer.PresetSpinner

@Composable
internal fun Equalizer(modifier: Modifier = Modifier) =
    Column(modifier) {
        PresetSpinner(Modifier.fillMaxWidth())
        Spacer(Modifier.height(15.dp))
        BandsWithCurve(Modifier.fillMaxWidth())
    }