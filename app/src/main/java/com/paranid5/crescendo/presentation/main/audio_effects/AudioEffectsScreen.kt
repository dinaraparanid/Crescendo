package com.paranid5.crescendo.presentation.main.audio_effects

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
import com.paranid5.crescendo.presentation.main.audio_effects.view.BassAndReverb
import com.paranid5.crescendo.presentation.main.audio_effects.view.Equalizer
import com.paranid5.crescendo.presentation.main.audio_effects.view.PitchAndSpeed
import com.paranid5.crescendo.presentation.main.audio_effects.view.UpBar

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
) = Column(modifier) {
    UpBar(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(10.dp))

    PitchAndSpeed(
        viewModel = viewModel,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
    )

    Spacer(Modifier.height(10.dp))

    Equalizer(
        viewModel = viewModel,
        modifier = Modifier
            .fillMaxWidth()
            .weight(3F)
    )

    BassAndReverbWithSpacerPortraitCompat(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BassAndReverbWithSpacerPortraitCompat(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier
) = when (Build.VERSION.SDK_INT) {
    Build.VERSION_CODES.Q ->
        Spacer(Modifier.height(100.dp))

    else -> {
        Spacer(Modifier.height(10.dp))

        BassAndReverb(
            viewModel = viewModel,
            modifier = modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun AudioEffectsScreenLandscape(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    UpBar(
        viewModel = viewModel,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(10.dp))

    AudioEffectsScreenLandscapeContent(
        viewModel = viewModel,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp)
    )
}

@Composable
private fun AudioEffectsScreenLandscapeContent(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = Row(modifier) {
    Equalizer(
        viewModel = viewModel,
        modifier = Modifier.weight(1F)
    )

    Spacer(Modifier.width(10.dp))

    SubEffectsLandscape(
        viewModel = viewModel,
        modifier = Modifier.weight(1F)
    )
}

@Composable
private fun SubEffectsLandscape(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier
) = Column(modifier) {
    PitchAndSpeed(
        viewModel = viewModel,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
    )

    BassAndReverbLandscapeCompat(
        viewModel = viewModel,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
            .padding(end = 60.dp)
    )
}

@Composable
private fun BassAndReverbLandscapeCompat(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier
) {
    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
        Spacer(Modifier.height(10.dp))

        BassAndReverb(
            viewModel = viewModel,
            modifier = modifier.fillMaxWidth()
        )
    }
}