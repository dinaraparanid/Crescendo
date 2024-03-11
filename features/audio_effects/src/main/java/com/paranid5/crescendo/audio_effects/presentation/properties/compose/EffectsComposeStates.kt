package com.paranid5.crescendo.audio_effects.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun AudioEffectsViewModel.collectAreAudioEffectsEnabledAsState(initial: Boolean = false) =
    areAudioEffectsEnabledFlow.collectLatestAsState(initial)

@Composable
internal fun AudioEffectsViewModel.collectBassStrengthAsState(initial: Short = 0) =
    bassStrengthFlow.collectLatestAsState(initial)

@Composable
internal fun AudioEffectsViewModel.collectBassStrengthAsNullableState(initial: Short? = null) =
    bassStrengthFlow.collectLatestAsState(initial)

@Composable
internal fun AudioEffectsViewModel.collectReverbPresetAsState(initial: Short = 0) =
    reverbPresetFlow.collectLatestAsState(initial)

@Composable
internal fun AudioEffectsViewModel.collectReverbPresetAsNullableState(initial: Short? = null) =
    reverbPresetFlow.collectLatestAsState(initial)

@Composable
internal fun AudioEffectsViewModel.collectPitchTextAsState(initial: String = "") =
    pitchTextFlow.collectLatestAsState(initial)

@Composable
internal fun AudioEffectsViewModel.collectPitchTextAsNullableState(initial: String? = null) =
    pitchTextFlow.collectLatestAsState(initial)

@Composable
internal fun AudioEffectsViewModel.collectSpeedTextAsState(initial: String = "") =
    speedTextState.collectLatestAsState(initial)

@Composable
internal fun AudioEffectsViewModel.collectSpeedTextAsNullableState(initial: String? = null) =
    speedTextState.collectLatestAsState(initial)