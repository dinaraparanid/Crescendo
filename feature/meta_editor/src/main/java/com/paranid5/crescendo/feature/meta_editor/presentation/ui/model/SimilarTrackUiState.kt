package com.paranid5.crescendo.feature.meta_editor.presentation.ui.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.domain.genius.model.GeniusTrack
import com.paranid5.crescendo.ui.covers.ImageContainer
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.toUiStateIfNotNull
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
internal data class SimilarTrackUiState private constructor(
    internal val title: String,
    internal val artists: String,
    internal val covers: List<ImageContainer.Bitmap>,
    internal val album: String? = null,
) : Parcelable {

    @IgnoredOnParcel
    internal val primaryCover: UiState<ImageContainer.Bitmap> =
        covers.find { it.value != null }.toUiStateIfNotNull()

    internal companion object {
        fun fromDTO(track: GeniusTrack, covers: List<ImageContainer.Bitmap>) = SimilarTrackUiState(
            title = track.title,
            artists = track.artists,
            album = track.album,
            covers = covers,
        )
    }
}