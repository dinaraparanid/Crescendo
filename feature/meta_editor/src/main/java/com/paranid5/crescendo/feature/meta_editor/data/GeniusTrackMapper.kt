package com.paranid5.crescendo.feature.meta_editor.data

import com.paranid5.crescendo.domain.genius.model.GeniusTrack
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState

fun GeniusTrack.toTrackUiState(numberInAlbum: Int) = TrackUiState(
    androidId = 0,
    title = title,
    artist = artists,
    album = album.orEmpty(),
    path = "",
    durationMillis = 0,
    displayName = "",
    dateAdded = 0,
    numberInAlbum = numberInAlbum,
)