package com.paranid5.crescendo.feature.meta_editor.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.cover.CoverRetriever
import com.paranid5.crescendo.domain.genius.GeniusApi
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.feature.meta_editor.presentation.ui.model.SimilarTrackUiState
import com.paranid5.crescendo.ui.covers.ImageContainer
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.toUiState
import com.paranid5.crescendo.ui.foundation.toUiStateError
import com.paranid5.crescendo.ui.foundation.toUiStateIfNotNull
import com.paranid5.crescendo.utils.doNothing
import com.paranid5.crescendo.utils.extensions.sideEffect
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

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

    private val _effectFlow = MutableSharedFlow<MetaEditorUiEffect>()
    override val effectFlow = _effectFlow.asSharedFlow()

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

        is MetaEditorUiIntent.Meta.SimilarCoverClicked ->
            updateState { copy(coverUiState = intent.cover.toUiState()) }

        is MetaEditorUiIntent.Meta.SimilarTrackClicked ->
            updateState {
                copy(
                    title = intent.track.title,
                    artist = intent.track.artists,
                    album = intent.track.album.orEmpty(),
                    coverUiState = intent.track.primaryCover,
                )
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
                    val tracksWithCovers = models.map { track ->
                        track to track.covers.map {
                            ImageContainer.Bitmap(coverRetriever.downloadCoverBitmap(it))
                        }
                    }

                    val tracksUiState = tracksWithCovers
                        .map { (track, covers) ->
                            // TODO: запрос номера альбома
                            SimilarTrackUiState.fromDTO(track = track, covers = covers)
                        }
                        .toUiState()

                    val coversUiState = tracksWithCovers
                        .flatMap { it.second }
                        .distinct()
                        .toUiState()

                    tracksUiState to coversUiState
                },
            )

        updateState {
            copy(similarTracksUiState = tracksUiState, similarCoversUiState = coversUiState)
        }
    }
}
