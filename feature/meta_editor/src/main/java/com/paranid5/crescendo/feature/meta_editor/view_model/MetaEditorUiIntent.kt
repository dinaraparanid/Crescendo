package com.paranid5.crescendo.feature.meta_editor.view_model

sealed interface MetaEditorUiIntent {
    sealed interface General : MetaEditorUiIntent {
        data object Back : General
        data object Apply : General
        data object KebabClick : General
    }

    sealed interface Lifecycle : MetaEditorUiIntent {
        data class OnCreate(val trackPath: String) : Lifecycle
        data object OnRefresh : Lifecycle
    }

    sealed interface Meta : MetaEditorUiIntent {
        data class UpdateTitle(val title: String) : Meta
        data class UpdateArtist(val artist: String) : Meta
        data class UpdateAlbum(val album: String) : Meta
        data class UpdateNumberInAlbum(val numberInAlbum: Int) : Meta
    }
}