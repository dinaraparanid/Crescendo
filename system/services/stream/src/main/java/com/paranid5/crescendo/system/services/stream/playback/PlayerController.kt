package com.paranid5.crescendo.system.services.stream.playback

import androidx.media3.common.Player
import com.paranid5.crescendo.data.sources.playback.RepeatingStatePublisher
import com.paranid5.crescendo.data.sources.playback.RepeatingStateSubscriber
import com.paranid5.system.services.common.playback.AudioEffectsController
import kotlinx.coroutines.flow.StateFlow

internal interface PlayerController :
    AudioEffectsController,
    RepeatingStateSubscriber,
    RepeatingStatePublisher {
    val player: Player

    var isPlaying: Boolean
    val isPlayingState: StateFlow<Boolean>

    val currentPositionState: StateFlow<Long>
    fun updateCurrentPosition()

    suspend fun setAndStoreRepeating(isRepeating: Boolean)

    fun playStreamViaPlayer(url: String, initialPosition: Long)

    fun pausePlayer()

    fun resumePlayer()

    fun seekToViaPlayer(position: Long)

    fun seekTenSecsBackViaPlayer()

    fun seekTenSecsForwardViaPlayer(videoDurationMillis: Long)

    fun resetAudioSessionIdIfNotPlaying()

    fun releasePlayerWithEffects()
}

internal inline var PlayerController.repeatMode
    get() = player.repeatMode
    set(value) {
        player.repeatMode = value
    }

internal var PlayerController.isRepeating
    get() = player.repeatMode == Player.REPEAT_MODE_ONE
    set(value) {
        player.repeatMode = repeatMode(value)
    }

internal fun repeatMode(isRepeating: Boolean) = when {
    isRepeating -> Player.REPEAT_MODE_ONE
    else -> Player.REPEAT_MODE_OFF
}