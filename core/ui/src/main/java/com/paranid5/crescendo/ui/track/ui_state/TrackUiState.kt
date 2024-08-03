package com.paranid5.crescendo.ui.track.ui_state

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.core.common.tracks.Track
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class TrackUiState(
    override val androidId: Long,
    override val title: String,
    override val artist: String,
    override val album: String,
    override val path: String,
    override val durationMillis: Long,
    override val displayName: String,
    override val dateAdded: Long,
    override val numberInAlbum: Int,
) : Parcelable, Track {
    companion object {
        fun fromDTO(track: Track) = TrackUiState(
            androidId = track.androidId,
            title = track.title,
            artist = track.artist,
            album = track.album,
            path = track.path,
            durationMillis = track.durationMillis,
            displayName = track.displayName,
            dateAdded = track.dateAdded,
            numberInAlbum = track.numberInAlbum,
        )
    }
}
