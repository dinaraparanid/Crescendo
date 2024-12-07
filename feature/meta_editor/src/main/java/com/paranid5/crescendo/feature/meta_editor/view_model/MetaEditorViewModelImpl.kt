package com.paranid5.crescendo.feature.meta_editor.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.cover.CoverRetriever
import com.paranid5.crescendo.domain.genius.GeniusApi
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.feature.meta_editor.data.toTrackUiState
import com.paranid5.crescendo.ui.covers.ImageContainer
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.toUiState
import com.paranid5.crescendo.ui.foundation.toUiStateError
import com.paranid5.crescendo.ui.foundation.toUiStateIfNotNull
import com.paranid5.crescendo.utils.doNothing
import com.paranid5.crescendo.utils.extensions.sideEffect

internal class MetaEditorViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val geniusApi: GeniusApi,
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
        is MetaEditorUiIntent.General -> onGeneralUiIntent(intent)
        is MetaEditorUiIntent.Lifecycle -> onLifecycleUiIntent(intent)
        is MetaEditorUiIntent.Meta -> onMetaUiIntent(intent)
    }

    private fun onGeneralUiIntent(intent: MetaEditorUiIntent.General) = when (intent) {
        is MetaEditorUiIntent.General.Apply -> doNothing() // TODO: update meta and exit
        is MetaEditorUiIntent.General.Back -> doNothing() // TODO: exit
        is MetaEditorUiIntent.General.KebabClick -> doNothing() // TODO: show kebab menu
    }

    private fun onLifecycleUiIntent(intent: MetaEditorUiIntent.Lifecycle) = when (intent) {
        is MetaEditorUiIntent.Lifecycle.Create -> viewModelScope.sideEffect {
            onCreate(trackPath = intent.trackPath)
        }

        is MetaEditorUiIntent.Lifecycle.Refresh -> doNothing() // TODO: fetch all data from network
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

        is MetaEditorUiIntent.Meta.SimilarCoverClicked -> {
            // TODO: change cover
        }
    }

    private suspend fun onCreate(trackPath: String) {
        updateState {
            copy(
                trackPathUiState = trackPath.toUiState(),
                coverUiState = UiState.Loading,
            )
        }

        viewModelScope.sideEffect { fetchTrackCover() }

        fetchTrackMeta()
        loadSimilarTracks(isInitialLoading = true)
    }

    private suspend fun fetchTrackCover() {
        val coverUiState = coverRetriever
            .retrieveCoverBitmap(path = state.requireTrackPath())
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

    private suspend fun loadSimilarTracks(isInitialLoading: Boolean) {
        updateState {
            copy(
                similarTracksUiState = when {
                    isInitialLoading -> UiState.Loading
                    else -> UiState.Refreshing.flattened(similarTracksUiState)
                },
                similarCoversUiState = when {
                    isInitialLoading -> UiState.Loading
                    else -> UiState.Refreshing.flattened(similarCoversUiState)
                },
            )
        }

        val (tracksUiState, coversUiState) = geniusApi
            .findSimilarTracks(titleInput = state.title, artistInput = state.artist)
            .fold(
                ifLeft = { it.toUiStateError() to it.toUiStateError() },
                ifRight = { models ->
                    val tracksUiState = models
                        .map { it.toTrackUiState(numberInAlbum = 0) } // TODO: запрос номера альбома
                        .toUiState()

                    val coversUiState = models
                        .map {
                            coverRetriever
                                .downloadCoverBitmap(*it.covers.toTypedArray())
                                .let(ImageContainer::Bitmap)
                        }
                        .toUiState()

                    tracksUiState to coversUiState
                },
            )

        updateState {
            copy(similarTracksUiState = tracksUiState, similarCoversUiState = coversUiState)
        }
    }
}
