package com.paranid5.crescendo.system.services.track.playback

import androidx.media3.common.Player
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.playback.RepeatingPublisher
import com.paranid5.crescendo.domain.playback.RepeatingSubscriber
import com.paranid5.system.services.common.playback.AudioEffectsController
import kotlinx.coroutines.flow.StateFlow

interface PlayerController :
    AudioEffectsController,
    RepeatingSubscriber,
    RepeatingPublisher {
    val player: Player
    //val mediaController: MediaController

    var isPlaying: Boolean
    val isPlayingState: StateFlow<Boolean>

    val currentPositionState: StateFlow<Long>

    fun fetchPositionFromPlayer()

    fun resetPosition()

    suspend fun updateAndStoreRepeating(isRepeating: Boolean)

    fun playPlaylistViaPlayer(
        playlist: List<Track>,
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

    fun addTrackToPlaylistViaPlayer(track: Track)

    fun removeTrackViaPlayer(index: Int): Int

    fun replacePlaylistViaPlayer(
        newPlaylist: List<Track>,
        newCurrentTrackIndex: Int
    )

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