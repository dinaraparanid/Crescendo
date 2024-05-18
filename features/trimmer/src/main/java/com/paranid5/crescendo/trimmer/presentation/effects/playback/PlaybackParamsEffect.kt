package com.paranid5.crescendo.trimmer.presentation.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectPitchAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectSpeedAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PlaybackParamsEffect(
    player: Player,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val pitch by viewModel.collectPitchAsState()
    val speed by viewModel.collectSpeedAsState()

    LaunchedEffect(pitch, speed, player) {
        player.playbackParameters = PlaybackParameters(speed, pitch)
    }
}