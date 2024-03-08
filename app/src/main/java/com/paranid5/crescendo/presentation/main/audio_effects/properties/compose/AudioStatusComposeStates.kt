package com.paranid5.crescendo.presentation.main.audio_effects.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState

@Composable
fun AudioEffectsViewModel.collectAudioStatusAsState(initial: AudioStatus? = null) =
    audioStatusFlow.collectLatestAsState(initial)