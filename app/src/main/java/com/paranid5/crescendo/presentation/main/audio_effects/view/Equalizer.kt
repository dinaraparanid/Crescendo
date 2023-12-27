package com.paranid5.crescendo.presentation.main.audio_effects.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.presentation.main.audio_effects.view.equalizer.BandsWithCurve
import com.paranid5.crescendo.presentation.main.audio_effects.view.equalizer.PresetSpinner

@Composable
fun Equalizer(viewModel: AudioEffectsViewModel, modifier: Modifier = Modifier) =
    Column(modifier) {
        PresetSpinner(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(15.dp))

        BandsWithCurve(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )
    }