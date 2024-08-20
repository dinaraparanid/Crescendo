package com.paranid5.crescendo.feature.stream.fetch.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.cover.CoverRetriever
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.ui.covers.ImageContainer
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.fold
import com.paranid5.crescendo.ui.foundation.toUiState
import com.paranid5.crescendo.ui.metadata.VideoMetadataUiState
import com.paranid5.crescendo.utils.doNothing
import com.paranid5.crescendo.utils.extensions.sideEffect
import kotlinx.coroutines.Dispatchers

internal class FetchStreamViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val streamRepository: StreamRepository,
    private val coverRetriever: CoverRetriever,
) : FetchStreamViewModel, ViewModel(), StatePublisher<FetchStreamState> {
    companion object {
        private const val StateKey = "state"
    }

    override val stateFlow = savedStateHandle.getStateFlow(StateKey, FetchStreamState())

    override fun updateState(func: FetchStreamState.() -> FetchStreamState) {
        savedStateHandle[StateKey] = func(state)
    }

    override fun onUiIntent(intent: FetchStreamUiIntent) = when (intent) {
        is FetchStreamUiIntent.UpdateUrl -> updateState { copy(url = intent.url) }
        is FetchStreamUiIntent.RefreshCover -> refreshCover()
        is FetchStreamUiIntent.Buttons -> onButtonsUiIntent(intent = intent)
    }

    private fun onButtonsUiIntent(intent: FetchStreamUiIntent.Buttons) = when (intent) {
        is FetchStreamUiIntent.Buttons.ContinueClick -> fetchMetadata()

        is FetchStreamUiIntent.Buttons.DownloadClick -> doNothing() // TODO: show dialog

        is FetchStreamUiIntent.Buttons.NextClick -> updateState {
            copy(
                videoMetadataUiState = UiState.Initial,
                coverUiState = UiState.Initial,
            )
        }

        is FetchStreamUiIntent.Buttons.PlayClick -> doNothing() // TODO: start playing
    }

    private fun fetchMetadata() = viewModelScope.sideEffect(Dispatchers.Default) {
        updateState {
            copy(
                videoMetadataUiState = UiState.Loading,
                coverUiState = UiState.Loading,
            )
        }

        val metaRes = streamRepository.getVideoMetadata(state.url)

        val videoMetaUiState = metaRes.fold(
            ifLeft = { UiState.Error(it.message) },
            ifRight = { VideoMetadataUiState.fromDTO(it).toUiState() },
        )

        updateState { copy(videoMetadataUiState = videoMetaUiState) }

        val coverUiState = metaRes.fold(
            ifLeft = { UiState.Error(it.message) },
            ifRight = { fetchCover(it.covers).toUiState() },
        )

        updateState { copy(coverUiState = coverUiState) }
    }

    private fun refreshCover() = viewModelScope.sideEffect(Dispatchers.Default) {
        val coverUiState = state.videoMetadataUiState.fold(
            ifPresent = { fetchCover(it.coversPaths).toUiState() },
            ifEmpty = { UiState.Error() },
        )

        updateState { copy(coverUiState = coverUiState) }
    }

    private suspend fun fetchCover(coversPaths: List<String>) =
        ImageContainer.Bitmap(coverRetriever.getVideoCoverBitmap(videoCovers = coversPaths))
}
