package com.paranid5.crescendo.playing.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.playing.view_model.PlayingViewModel
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun PlayingViewModel.collectAudioStatusAsState(initial: AudioStatus? = null) =
    audioStatusFlow.collectLatestAsState(initial)