package com.paranid5.crescendo.feature.stream.fetch.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.image.ImageRetriever
import com.paranid5.crescendo.domain.image.model.Image
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor
import com.paranid5.crescendo.ui.covers.ImageContainer
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.fold
import com.paranid5.crescendo.ui.foundation.toUiState
import com.paranid5.crescendo.utils.extensions.sideEffect
import com.paranid5.feature.metadata.VideoMetadataUiState
import kotlinx.coroutines.Dispatchers

internal class FetchStreamViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val streamRepository: StreamRepository,
    private val playbackRepository: PlaybackRepository,
    private val imageRetriever: ImageRetriever,
    private val streamServiceAccessor: StreamServiceAccessor,
) : ViewModel(), FetchStreamViewModel, StatePublisher<FetchStreamState> {
    companion object {
        private const val StateKey = "state"
    }

    override val stateFlow = savedStateHandle.getStateFlow(StateKey, FetchStreamState())

    override fun updateState(func: FetchStreamState.() -> FetchStreamState) {
        savedStateHandle[StateKey] = func(state)
    }

    override fun onUiIntent(intent: FetchStreamUiIntent) = when (intent) {
        is FetchStreamUiIntent.UpdateUrl -> updateState { copy(url = intent.url) }
        is FetchStreamUiIntent.Buttons -> onButtonsUiIntent(intent = intent)
        is FetchStreamUiIntent.Retry -> onRetryUiIntent(intent = intent)
    }

    private fun onButtonsUiIntent(intent: FetchStreamUiIntent.Buttons) = when (intent) {
        is FetchStreamUiIntent.Buttons.ContinueClick -> fetchMetadata()
        is FetchStreamUiIntent.Buttons.NextClick -> resetUiStates()
        is FetchStreamUiIntent.Buttons.StartStreaming -> startStreaming()
    }

    private fun onRetryUiIntent(intent: FetchStreamUiIntent.Retry) = when (intent) {
        is FetchStreamUiIntent.Retry.ClearMetaOnFailure -> resetUiStates()
        is FetchStreamUiIntent.Retry.RefreshCover -> refreshCover()
    }

    private fun resetUiStates() = updateState {
        copy(
            videoMetadataUiState = UiState.Initial,
            coverUiState = UiState.Initial,
        )
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
            ifRight = { fetchCover(it.covers.map { it as Image.Url }).toUiState() },
        )

        updateState { copy(coverUiState = coverUiState) }
    }

    private fun refreshCover() = viewModelScope.sideEffect(Dispatchers.Default) {
        val coverUiState = state.videoMetadataUiState.fold(
            ifPresent = { fetchCover(it.coversUrls.map { it as Image.Url }).toUiState() },
            ifEmpty = { UiState.Error() },
        )

        updateState { copy(coverUiState = coverUiState) }
    }

    @Deprecated("Will be removed")
    private suspend fun fetchCover(coversUrls: List<Image.Url>) =
        ImageContainer.Bitmap(imageRetriever.downloadBitmap(url = coversUrls.first().value))

    private fun startStreaming() = viewModelScope.sideEffect {
        playbackRepository.updateAudioStatus(PlaybackStatus.STREAMING)
        streamRepository.updatePlayingUrl(state.url)
        streamServiceAccessor.startStreaming(state.url)
    }
}
