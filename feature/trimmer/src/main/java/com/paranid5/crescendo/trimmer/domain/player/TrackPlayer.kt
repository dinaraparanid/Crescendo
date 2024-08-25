package com.paranid5.crescendo.trimmer.domain.player

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

private const val TRANSITION_DURATION = 10_000L

internal fun PlayerStateChangedListener(
    onPlaybackLaunched: () -> Unit,
    onCompletion: () -> Unit,
) = object : Player.Listener {
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        if (isPlaying) onPlaybackLaunched()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if (playbackState == ExoPlayer.STATE_ENDED)
            onCompletion()
    }
}

internal fun Player.seekTenSecsBack(startPosition: Long) =
    seekTo(maxOf(currentPosition - TRANSITION_DURATION, startPosition))

internal fun Player.seekTenSecsForward(totalDuration: Long) =
    seekTo(minOf(currentPosition + TRANSITION_DURATION, totalDuration))

internal fun Player.stopAndReleaseCatching() = runCatching {
    stop()
    release()
}
