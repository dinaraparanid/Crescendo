package com.paranid5.crescendo.domain.interactors

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.data.sources.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistStatePublisher
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexStatePublisher
import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import com.paranid5.crescendo.system.services.track.TrackServiceStart
import kotlinx.collections.immutable.ImmutableList

data object TracksInteractor {
    suspend fun <VM> startPlaylistPlayback(
        newTracks: ImmutableList<Track>,
        newTrackIndex: Int,
        currentTrack: Track?,
        viewModel: VM,
        trackServiceAccessor: TrackServiceAccessor,
    ) where VM : AudioStatusStatePublisher,
            VM : CurrentPlaylistStatePublisher,
            VM : CurrentTrackIndexStatePublisher {
        viewModel.setAudioStatus(AudioStatus.PLAYING)
        viewModel.setCurrentPlaylist(newTracks)
        viewModel.setCurrentTrackIndex(newTrackIndex)

        val newCurrentTrack = newTracks.getOrNull(newTrackIndex)
        val startType = startType(currentTrack, newCurrentTrack)
        trackServiceAccessor.startPlaying(startType)
    }
}

private fun startType(
    currentTrack: Track?,
    newCurrentTrack: Track?,
) = when {
    currentTrack?.path == newCurrentTrack?.path -> TrackServiceStart.RESUME
    else -> TrackServiceStart.NEW_PLAYLIST
}