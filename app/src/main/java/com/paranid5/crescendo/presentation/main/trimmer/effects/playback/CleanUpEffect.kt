package com.paranid5.crescendo.presentation.main.trimmer.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.media3.common.Player
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.player.stopAndReleaseCatching
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectIsPlayerInitializedAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun CleanUpEffect(
    player: Player,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val isPlayerInitialized by viewModel.collectIsPlayerInitializedAsState()

    DisposableEffect(Unit) {
        onDispose {
            if (isPlayerInitialized) {
                viewModel.releasePlaybackPosMonitorTask()
                player.stopAndReleaseCatching()
            }

            viewModel.resetPlaybackStates()
        }
    }
}