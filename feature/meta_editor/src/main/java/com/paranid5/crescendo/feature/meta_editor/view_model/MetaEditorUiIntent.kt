package com.paranid5.crescendo.feature.meta_editor.view_model

import com.paranid5.crescendo.feature.meta_editor.presentation.ui.model.SimilarTrackUiState
import com.paranid5.crescendo.ui.covers.ImageContainer

sealed interface MetaEditorUiIntent {
    sealed interface General : MetaEditorUiIntent {
        data object Back : General
        data object Apply : General
        data object KebabClick : General
    }

    sealed interface Lifecycle : MetaEditorUiIntent {
        data object Refresh : Lifecycle
        data class Create internal constructor(internal val trackPath: String) : Lifecycle
    }

    sealed interface Meta : MetaEditorUiIntent {
        data class UpdateTitle internal constructor(internal val title: String) : Meta
        data class UpdateArtist internal constructor(internal val artist: String) : Meta
        data class UpdateAlbum internal constructor(internal val album: String) : Meta
        data class UpdateNumberInAlbum internal constructor(internal val numberInAlbum: Int) : Meta
        data class SimilarCoverClicked internal constructor(internal val cover: ImageContainer) : Meta
        data class SimilarTrackClicked internal constructor(internal val track: SimilarTrackUiState) : Meta
    }
}