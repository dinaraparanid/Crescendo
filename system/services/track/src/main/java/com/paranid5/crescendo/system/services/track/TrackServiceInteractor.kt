package com.paranid5.crescendo.system.services.track

import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track

interface TrackServiceInteractor {
    fun addToPlaylist(track: DefaultTrack)

    fun removeFromPlaylist(trackInd: Int)

    fun updatePlaylistAfterDrag()

    fun startPlaying(startType: TrackServiceStart)

    fun sendSwitchToPrevTrackBroadcast()

    fun sendSwitchToNextTrackBroadcast()

    fun sendSeekToBroadcast(position: Long)

    fun sendPauseBroadcast()

    fun startStreamingOrSendResumeBroadcast()

    fun sendChangeRepeatBroadcast()
}

fun TrackServiceInteractor.startPlaylistPlayback(
    nextTrack: Track?,
    prevTrack: Track?,
) {
    val startType = startType(prevTrack, nextTrack)
    startPlaying(startType)
}

private fun startType(
    nextTrack: Track?,
    prevTrack: Track?,
) = when {
    nextTrack?.path == prevTrack?.path -> TrackServiceStart.RESUME
    else -> TrackServiceStart.NEW_TRACK
}