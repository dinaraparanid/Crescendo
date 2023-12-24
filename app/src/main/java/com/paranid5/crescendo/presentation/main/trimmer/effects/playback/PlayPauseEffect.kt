package com.paranid5.crescendo.presentation.main.trimmer.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.media3.common.Player
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.player.PlaybackPositionMonitoringTask
import com.paranid5.crescendo.presentation.main.trimmer.properties.isPlayerInitializedState
import com.paranid5.crescendo.presentation.main.trimmer.properties.isPlayingState
import com.paranid5.crescendo.presentation.main.trimmer.properties.launchPlaybackPosMonitorTask
import com.paranid5.crescendo.presentation.main.trimmer.properties.setPlayerInitialized
import com.paranid5.crescendo.presentation.main.trimmer.properties.startPosInMillisState

@Composable
fun PlayPauseEffect(
    player: Player,
    viewModel: TrimmerViewModel
) {
    val isPlayerInitialized by viewModel.isPlayerInitializedState.collectAsState()
    val isPlaying by viewModel.isPlayingState.collectAsState()
    val startPos by viewModel.startPosInMillisState.collectAsState()

    LaunchedEffect(isPlaying) {
        when {
            isPlaying -> {
                viewModel.setPlayerInitialized(true)
                player.seekTo(startPos)
                player.playWhenReady = true
            }

            isPlayerInitialized -> {
                player.pause()
                viewModel.resetPlaybackPosition()
            }
        }
    }
}