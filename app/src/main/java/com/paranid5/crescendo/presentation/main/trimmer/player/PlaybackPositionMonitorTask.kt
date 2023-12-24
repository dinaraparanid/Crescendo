package com.paranid5.crescendo.presentation.main.trimmer.player

import androidx.media3.common.Player
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.setPlaybackPosInMillis
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

private const val PLAYBACK_UPDATE_COOLDOWN = 500L

suspend fun PlaybackPositionMonitoringTask(
    player: Player,
    trimmerViewModel: TrimmerViewModel
) = coroutineScope {
    while (player.isPlaying) {
        trimmerViewModel.setPlaybackPosInMillis(player.currentPosition)
        delay(PLAYBACK_UPDATE_COOLDOWN)
    }
}