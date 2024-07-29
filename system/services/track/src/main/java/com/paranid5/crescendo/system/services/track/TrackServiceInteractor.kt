package com.paranid5.crescendo.system.services.track

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexPublisher

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

suspend fun <S> TrackServiceInteractor.startPlaylistPlayback(
    newTracks: List<Track>,
    newTrackIndex: Int,
    currentTrack: Track?,
    source: S,
) where S : AudioStatusPublisher,
        S : CurrentPlaylistPublisher,
        S : CurrentTrackIndexPublisher {
    source.updateAudioStatus(AudioStatus.PLAYING)
    source.updateCurrentPlaylist(newTracks)
    source.updateCurrentTrackIndex(newTrackIndex)

    val newCurrentTrack = newTracks.getOrNull(newTrackIndex)
    val startType = startType(currentTrack, newCurrentTrack)
    startPlaying(startType)
}

private fun startType(
    currentTrack: Track?,
    newCurrentTrack: Track?,
) = when {
    currentTrack?.path == newCurrentTrack?.path -> TrackServiceStart.RESUME
    else -> TrackServiceStart.NEW_TRACK
}