package com.paranid5.crescendo.data.current_playlist

import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.data.CurrentPlaylistTrack

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