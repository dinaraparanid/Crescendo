package com.paranid5.crescendo.feature.meta_editor.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.ui.covers.ImageContainer
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.getOrNull
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class MetaEditorState(
    val trackPathUiState: UiState<String> = UiState.Initial,
    val coverUiState: UiState<ImageContainer> = UiState.Initial,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val numberInAlbum: Int = UndefinedNumberInAlbum,

    @IgnoredOnParcel
    val similarCoversUiState: UiState<ImmutableList<ImageContainer>> = UiState.Initial,
) : Parcelable {
    companion object {
        const val UndefinedNumberInAlbum = -1
    }

    @IgnoredOnParcel
    val trackPath = trackPathUiState.getOrNull()
    fun requireTrackPath() = requireNotNull(trackPath)
}
