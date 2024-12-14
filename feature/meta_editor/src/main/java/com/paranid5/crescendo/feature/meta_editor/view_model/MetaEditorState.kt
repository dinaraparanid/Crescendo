package com.paranid5.crescendo.feature.meta_editor.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.feature.meta_editor.presentation.ui.model.SimilarTrackUiState
import com.paranid5.crescendo.ui.covers.ImageContainer
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.getOrNull
import com.paranid5.crescendo.utils.extensions.orNil
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class MetaEditorState internal constructor(
    internal val trackPathUiState: UiState<String> = UiState.Initial,
    internal val coverUiState: UiState<ImageContainer> = UiState.Initial,
    internal val title: String = "",
    internal val artist: String = "",
    internal val album: String = "",
    internal val numberInAlbum: Int = UndefinedNumberInAlbum,
    internal val similarTracksUiState: UiState<List<SimilarTrackUiState>> = UiState.Initial,
    internal val similarCoversUiState: UiState<List<ImageContainer>> = UiState.Initial,
) : Parcelable {
    internal companion object {
        const val UndefinedNumberInAlbum = -1
    }

    @IgnoredOnParcel
    internal val trackPath = trackPathUiState.getOrNull()
    internal fun requireTrackPath() = requireNotNull(trackPath)

    @IgnoredOnParcel
    internal val similarTracks = similarTracksUiState.getOrNull().orNil()
}
