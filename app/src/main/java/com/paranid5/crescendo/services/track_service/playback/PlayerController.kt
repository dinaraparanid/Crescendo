package com.paranid5.crescendo.services.track_service.playback

import androidx.media3.common.Player
import com.paranid5.crescendo.data.states.playback.RepeatingStatePublisher
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriber
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.services.core.playback.AudioEffectsController
import kotlinx.coroutines.flow.StateFlow

interface PlayerController :
    AudioEffectsController,
    RepeatingStateSubscriber,
    RepeatingStatePublisher {
    val player: Player

    var isPlaying: Boolean
    val isPlayingState: StateFlow<Boolean>

    val currentPositionState: StateFlow<Long>
    fun updateCurrentPosition()

    suspend fun setAndStoreRepeating(isRepeating: Boolean)

    fun playPlaylistViaPlayer(
        playlist: List<com.paranid5.crescendo.core.common.tracks.Track>,
        currentTrackIndex: Int,
        initialPosition: Long
    )

    fun pausePlayer()

    fun resumePlayer()

    fun seekToViaPlayer(position: Long)

    fun seekToPreviousTrackViaPlayer(playlistSize: Int): Int

    fun seekToNextTrackViaPlayer(): Int

    val currentMediaItemIndex: Int

    fun resetAudioSessionIdIfNotPlaying()

    fun addTrackToPlaylistViaPlayer(track: com.paranid5.crescendo.core.common.tracks.Track)

    fun removeTrackViaPlayer(index: Int): Int

    fun replacePlaylistViaPlayer(newPlaylist: List<com.paranid5.crescendo.core.common.tracks.Track>, newCurrentTrackIndex: Int)

    fun releasePlayerWithEffects()
}

inline var PlayerController.repeatMode
    get() = player.repeatMode
    set(value) {
        player.repeatMode = value
    }

var PlayerController.isRepeating
    get() = player.repeatMode == Player.REPEAT_MODE_ONE
    set(value) {
        player.repeatMode = repeatMode(value)
    }

internal fun repeatMode(isRepeating: Boolean) = when {
    isRepeating -> Player.REPEAT_MODE_ONE
    else -> Player.REPEAT_MODE_ALL
}