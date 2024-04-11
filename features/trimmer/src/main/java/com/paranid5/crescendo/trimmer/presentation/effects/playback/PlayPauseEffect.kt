package com.paranid5.crescendo.trimmer.presentation.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.media3.common.Player
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectIsPlayerInitializedAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectIsPlayingAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectStartPosInMillisAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PlayPauseEffect(
    player: Player,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val isPlayerInitialized by viewModel.collectIsPlayerInitializedAsState()
    val isPlaying by viewModel.collectIsPlayingAsState()
    val startPos by viewModel.collectStartPosInMillisAsState()

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