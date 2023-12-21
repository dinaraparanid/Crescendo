package com.paranid5.crescendo.presentation.main.trimmer.player

import android.media.MediaPlayer
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.setPlaybackPosInMillis
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

private const val PLAYBACK_UPDATE_COOLDOWN = 500L

suspend fun PlaybackPositionMonitoringTask(
    player: MediaPlayer,
    trimmerViewModel: TrimmerViewModel
) = coroutineScope {
    while (player.isPlaying) {
        trimmerViewModel.setPlaybackPosInMillis(player.currentPosition.toLong())
        delay(PLAYBACK_UPDATE_COOLDOWN)
    }
}