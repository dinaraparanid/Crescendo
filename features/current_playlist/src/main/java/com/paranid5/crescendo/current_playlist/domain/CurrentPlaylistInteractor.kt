package com.paranid5.crescendo.current_playlist.domain

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.current_playlist.data.TrackDismissDataSource
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistStatePublisher
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexStatePublisher
import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay

internal fun tryDismissTrack(
    source: TrackDismissDataSource,
    index: Int,
    track: Track,
    currentPlaylist: ImmutableList<Track>,
    currentTrackIndex: Int,
): Boolean {
    if (index == currentTrackIndex)
        return false

    source.setPlaylistDismissMediator(
        (currentPlaylist.take(index) + currentPlaylist.drop(index + 1))
            .toImmutableList()
    )

    source.setTrackIndexDismissMediator(index)
    source.setTrackPathDismissKey(track.path)
    return true
}

internal suspend fun <P> updateCurrentPlaylistAfterDrag(
    publisher: P,
    newTracks: ImmutableList<Track>,
    newCurTrackIndex: Int,
    trackServiceAccessor: TrackServiceAccessor
) where P : CurrentTrackIndexStatePublisher, P : CurrentPlaylistStatePublisher {
    publisher.setCurrentTrackIndex(newCurTrackIndex)
    publisher.setCurrentPlaylist(newTracks)

    delay(500) // small delay to complete transaction and update event flow
    trackServiceAccessor.updatePlaylistAfterDrag()
}