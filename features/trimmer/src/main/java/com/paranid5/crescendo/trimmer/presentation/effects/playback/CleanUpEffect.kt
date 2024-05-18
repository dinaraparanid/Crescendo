package com.paranid5.crescendo.trimmer.presentation.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.media3.common.Player
import com.paranid5.crescendo.trimmer.domain.player.stopAndReleaseCatching
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun CleanUpEffect(
    player: Player,
    viewModel: TrimmerViewModel = koinViewModel(),
) = DisposableEffect(player) {
    onDispose {
        viewModel.releasePlaybackPosMonitorTask()
        player.stopAndReleaseCatching()
        viewModel.resetPlaybackStates()
    }
}