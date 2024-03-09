package com.paranid5.crescendo.presentation.main.audio_effects.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
fun AudioEffectsViewModel.collectAreAudioEffectsEnabledAsState(initial: Boolean = false) =
    areAudioEffectsEnabledFlow.collectLatestAsState(initial)

@Composable
fun AudioEffectsViewModel.collectBassStrengthAsState(initial: Short = 0) =
    bassStrengthFlow.collectLatestAsState(initial)

@Composable
fun AudioEffectsViewModel.collectBassStrengthAsNullableState(initial: Short? = null) =
    bassStrengthFlow.collectLatestAsState(initial)

@Composable
fun AudioEffectsViewModel.collectReverbPresetAsState(initial: Short = 0) =
    reverbPresetFlow.collectLatestAsState(initial)

@Composable
fun AudioEffectsViewModel.collectReverbPresetAsNullableState(initial: Short? = null) =
    reverbPresetFlow.collectLatestAsState(initial)

@Composable
fun AudioEffectsViewModel.collectPitchTextAsState(initial: String = "") =
    pitchTextFlow.collectLatestAsState(initial)

@Composable
fun AudioEffectsViewModel.collectPitchTextAsNullableState(initial: String? = null) =
    pitchTextFlow.collectLatestAsState(initial)

@Composable
fun AudioEffectsViewModel.collectSpeedTextAsState(initial: String = "") =
    speedTextState.collectLatestAsState(initial)

@Composable
fun AudioEffectsViewModel.collectSpeedTextAsNullableState(initial: String? = null) =
    speedTextState.collectLatestAsState(initial)