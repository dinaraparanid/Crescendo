package com.paranid5.crescendo.core.impl.tracks

import android.os.Parcelable
import com.paranid5.crescendo.core.common.tracks.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class DefaultTrackModel(
    override val androidId: Long,
    override val title: String,
    override val artist: String,
    override val album: String,
    override val path: String,
    override val durationMillis: Long,
    override val displayName: String,
    override val dateAdded: Long,
    override val numberInAlbum: Int
) : Parcelable, Track {
    constructor(track: Track) : this(
        androidId = track.androidId,
        title = track.title,
        artist = track.artist,
        album = track.album,
        path = track.path,
        durationMillis = track.durationMillis,
        displayName = track.displayName,
        dateAdded = track.dateAdded,
        numberInAlbum = track.numberInAlbum
    )
}
