package com.paranid5.crescendo.data.current_playlist

import com.paranid5.crescendo.data.CurrentPlaylistTrack
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.domain.tracks.Track

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
    )