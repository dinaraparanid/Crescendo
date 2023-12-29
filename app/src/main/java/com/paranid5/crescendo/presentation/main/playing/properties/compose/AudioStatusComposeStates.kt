package com.paranid5.crescendo.presentation.main.playing.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.audioStatusFlow
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState

@Composable
fun PlayingViewModel.collectAudioStatusAsState(initial: AudioStatus? = null) =
    audioStatusFlow.collectLatestAsState(initial)