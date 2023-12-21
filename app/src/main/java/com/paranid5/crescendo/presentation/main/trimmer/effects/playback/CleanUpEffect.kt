package com.paranid5.crescendo.presentation.main.trimmer.effects.playback

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.player.stopAndReleaseCatching
import com.paranid5.crescendo.presentation.main.trimmer.properties.isPlayerInitializedState
import com.paranid5.crescendo.presentation.main.trimmer.properties.releasePlaybackPosMonitorTask

@Composable
fun CleanUpEffect(
    player: MediaPlayer,
    viewModel: TrimmerViewModel
) {
    val isPlayerInitialized by viewModel.isPlayerInitializedState.collectAsState()

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