package com.paranid5.mediastreamer.presentation.audio_effects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.mediastreamer.presentation.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun AudioEffectsScreen(
    viewModel: AudioEffectsViewModel,
    curScreenState: MutableStateFlow<Screens>,
    modifier: Modifier = Modifier,
) {
    curScreenState.update { Screens.MainScreens.StreamScreens.AudioEffects }

    Column(modifier) {
        UpBar(Modifier.fillMaxWidth())
        PitchAndSpeed(viewModel, Modifier.fillMaxWidth())
        Equalizer(Modifier.fillMaxWidth())
    }
}