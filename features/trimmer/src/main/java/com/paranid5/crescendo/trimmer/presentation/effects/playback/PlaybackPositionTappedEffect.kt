package com.paranid5.crescendo.trimmer.presentation.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.media3.common.Player
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerPositionBroadcast
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun PlaybackPositionTappedEffect(player: Player) {
    val positionBroadcast = LocalTrimmerPositionBroadcast.current

    LaunchedEffect(Unit) {
        positionBroadcast.collectLatest {
            player.seekTo(it)
        }
    }
}