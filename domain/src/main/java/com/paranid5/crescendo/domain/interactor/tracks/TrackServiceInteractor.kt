package com.paranid5.crescendo.domain.interactor.tracks

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexPublisher
import kotlinx.collections.immutable.ImmutableList

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
    newTracks: ImmutableList<Track>,
    newTrackIndex: Int,
    currentTrack: Track?,
    source: S,
) where S : AudioStatusPublisher,
        S : CurrentPlaylistPublisher,
        S : CurrentTrackIndexPublisher {
    source.setAudioStatus(AudioStatus.PLAYING)
    source.setCurrentPlaylist(newTracks)
    source.setCurrentTrackIndex(newTrackIndex)

    val newCurrentTrack = newTracks.getOrNull(newTrackIndex)
    val startType = startType(currentTrack, newCurrentTrack)
    startPlaying(startType)
}

private fun startType(
    currentTrack: Track?,
    newCurrentTrack: Track?,
) = when {
    currentTrack?.path == newCurrentTrack?.path -> TrackServiceStart.RESUME
    else -> TrackServiceStart.NEW_PLAYLIST
}