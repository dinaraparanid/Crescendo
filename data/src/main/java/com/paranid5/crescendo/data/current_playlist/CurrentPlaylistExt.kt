package com.paranid5.crescendo.data.current_playlist

import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.data.CurrentPlaylistTrack

private const val TIMESTAMP_NOT_IMPORTANT = 0L

internal fun CurrentPlaylistTrack.toTrack(): Track =
    DefaultTrack(
        androidId = androidId,
        title = title,
        artist = artist,
        album = album,
        path = path,
        durationMillis = durationMillis,
        displayName = displayName,
        dateAdded = dateAdded,
        numberInAlbum = numberInAlbum.toInt(),
        timestamp = TIMESTAMP_NOT_IMPORTANT,
    )

internal fun List<CurrentPlaylistTrack>.toTracks(): List<Track> =
    map(CurrentPlaylistTrack::toTrack)
