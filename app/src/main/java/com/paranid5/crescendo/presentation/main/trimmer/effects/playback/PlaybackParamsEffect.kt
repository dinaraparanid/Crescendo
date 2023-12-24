package com.paranid5.crescendo.presentation.main.trimmer.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.pitchState
import com.paranid5.crescendo.presentation.main.trimmer.properties.speedState

@Composable
fun PlaybackParamsEffect(player: Player, viewModel: TrimmerViewModel) {
    val pitch by viewModel.pitchState.collectAsState()
    val speed by viewModel.speedState.collectAsState()

    LaunchedEffect(pitch, speed) {
        player.playbackParameters = PlaybackParameters(speed, pitch)
    }
}