package com.paranid5.crescendo.feature.meta_editor.view_model

import com.paranid5.crescendo.ui.covers.ImageContainer

sealed interface MetaEditorUiIntent {
    sealed interface General : MetaEditorUiIntent {
        data object Back : General
        data object Apply : General
        data object KebabClick : General
    }

    sealed interface Lifecycle : MetaEditorUiIntent {
        data class Create(val trackPath: String) : Lifecycle
        data object Refresh : Lifecycle
    }

    sealed interface Meta : MetaEditorUiIntent {
        data class UpdateTitle(val title: String) : Meta
        data class UpdateArtist(val artist: String) : Meta
        data class UpdateAlbum(val album: String) : Meta
        data class UpdateNumberInAlbum(val numberInAlbum: Int) : Meta
        data class SimilarCoverClicked(val cover: ImageContainer) : Meta
    }
}