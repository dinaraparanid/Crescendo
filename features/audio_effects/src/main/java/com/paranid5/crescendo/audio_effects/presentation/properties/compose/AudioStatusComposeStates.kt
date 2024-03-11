package com.paranid5.crescendo.audio_effects.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun AudioEffectsViewModel.collectAudioStatusAsState(initial: AudioStatus? = null) =
    audioStatusFlow.collectLatestAsState(initial)