package com.paranid5.crescendo.feature.meta_editor.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.cover.CoverRetriever
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.ui.covers.ImageContainer
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.toUiState
import com.paranid5.crescendo.ui.foundation.toUiStateIfNotNull
import com.paranid5.crescendo.utils.doNothing
import com.paranid5.crescendo.utils.extensions.sideEffect

internal class MetaEditorViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val tracksRepository: TracksRepository,
    private val coverRetriever: CoverRetriever,
) : ViewModel(), MetaEditorViewModel, StatePublisher<MetaEditorState> {
    companion object {
        private const val StateKey = "state"
    }

    override val stateFlow = savedStateHandle.getStateFlow(StateKey, MetaEditorState())

    override fun updateState(func: MetaEditorState.() -> MetaEditorState) {
        savedStateHandle[StateKey] = func(state)
    }

    override fun onUiIntent(intent: MetaEditorUiIntent) = when (intent) {
        is MetaEditorUiIntent.Lifecycle -> onLifecycleUiIntent(intent)
        is MetaEditorUiIntent.Meta -> onMetaUiIntent(intent)
    }

    private fun onLifecycleUiIntent(intent: MetaEditorUiIntent.Lifecycle) = when (intent) {
        is MetaEditorUiIntent.Lifecycle.OnCreate -> onCreate(trackPath = intent.trackPath)
        is MetaEditorUiIntent.Lifecycle.OnRefresh -> doNothing() // TODO: fetch all data from network
    }

    private fun onMetaUiIntent(intent: MetaEditorUiIntent.Meta) = when (intent) {
        is MetaEditorUiIntent.Meta.UpdateAlbum ->
            updateState { copy(album = intent.album) }

        is MetaEditorUiIntent.Meta.UpdateArtist ->
            updateState { copy(artist = intent.artist) }

        is MetaEditorUiIntent.Meta.UpdateNumberInAlbum ->
            updateState { copy(numberInAlbum = intent.numberInAlbum) }

        is MetaEditorUiIntent.Meta.UpdateTitle ->
            updateState { copy(title = intent.title) }
    }

    private fun onCreate(trackPath: String) {
        updateState {
            copy(
                trackPathUiState = trackPath.toUiState(),
                coverUiState = UiState.Loading,
            )
        }

        viewModelScope.sideEffect { fetchTrackCover() }
        viewModelScope.sideEffect { fetchTrackMeta() }

        // TODO: Fetch data from network
    }

    private suspend fun fetchTrackCover() {
        val coverUiState = coverRetriever
            .getTrackCoverBitmap(trackPath = state.requireTrackPath())
            ?.let(ImageContainer::Bitmap)
            .toUiStateIfNotNull()

        updateState { copy(coverUiState = coverUiState) }
    }

    private suspend fun fetchTrackMeta() {
        tracksRepository
            .getTrackFromMediaStore(trackPath = state.requireTrackPath())
            ?.let { track ->
                updateState {
                    copy(
                        title = track.title,
                        artist = track.artist,
                        album = track.album,
                        numberInAlbum = track.numberInAlbum,
                    )
                }
            }
    }
}
