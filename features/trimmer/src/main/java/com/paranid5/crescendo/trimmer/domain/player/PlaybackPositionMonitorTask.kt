package com.paranid5.crescendo.trimmer.domain.player

import androidx.media3.common.Player
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

private const val PLAYBACK_UPDATE_COOLDOWN = 500L

internal suspend fun PlaybackPositionMonitoringTask(
    player: Player,
    trimmerViewModel: TrimmerViewModel
) = coroutineScope {
    while (player.isPlaying) {
        trimmerViewModel.setPlaybackPosInMillis(player.currentPosition)
        delay(PLAYBACK_UPDATE_COOLDOWN)
    }
}