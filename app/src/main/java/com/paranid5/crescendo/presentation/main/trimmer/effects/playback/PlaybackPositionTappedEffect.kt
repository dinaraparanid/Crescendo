package com.paranid5.crescendo.presentation.main.trimmer.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.media3.common.Player
import com.paranid5.crescendo.core.impl.presentation.composition_locals.trimmer.LocalTrimmerPositionBroadcast
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PlaybackPositionTappedEffect(player: Player) {
    val positionBroadcast = LocalTrimmerPositionBroadcast.current

    LaunchedEffect(Unit) {
        positionBroadcast.collectLatest {
            player.seekTo(it)
        }
    }
}