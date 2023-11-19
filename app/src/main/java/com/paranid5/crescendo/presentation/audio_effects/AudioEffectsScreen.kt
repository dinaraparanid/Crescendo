package com.paranid5.crescendo.presentation.audio_effects

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun AudioEffectsScreen(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> AudioEffectsScreenLandscape(viewModel, modifier)
    else -> AudioEffectsScreenPortrait(viewModel, modifier)
}

@Composable
private fun AudioEffectsScreenPortrait(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = Column(modifier.padding(horizontal = 10.dp)) {
    UpBar(Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))

    PitchAndSpeed(viewModel, Modifier.fillMaxWidth().weight(1F))
    Spacer(Modifier.height(10.dp))

    Equalizer(Modifier.fillMaxWidth().weight(3F))

    when (Build.VERSION.SDK_INT) {
        Build.VERSION_CODES.Q -> Spacer(Modifier.height(100.dp))

        else -> {
            Spacer(Modifier.height(10.dp))
            BassAndReverb(Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun AudioEffectsScreenLandscape(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = Column(modifier.padding(horizontal = 10.dp)) {
    UpBar(Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))

    Row(Modifier.fillMaxSize().padding(bottom = 10.dp)) {
        Equalizer(Modifier.weight(1F))
        Spacer(Modifier.width(10.dp))

        Column(Modifier.weight(1F)) {
            PitchAndSpeed(viewModel, Modifier.fillMaxWidth().weight(1F))

            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
                Spacer(Modifier.height(10.dp))
                BassAndReverb(Modifier.fillMaxWidth().weight(1F).padding(end = 60.dp))
            }
        }
    }
}