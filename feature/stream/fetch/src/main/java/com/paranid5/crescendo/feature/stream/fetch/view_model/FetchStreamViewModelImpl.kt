package com.paranid5.crescendo.feature.stream.fetch.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.toUiState
import com.paranid5.crescendo.ui.metadata.VideoMetadataUiState
import com.paranid5.crescendo.utils.extensions.sideEffect
import kotlinx.coroutines.Dispatchers

internal class FetchStreamViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val streamRepository: StreamRepository,
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
        is FetchStreamUiIntent.ContinueClick -> fetchMetadata()
    }

    private fun fetchMetadata() = viewModelScope.sideEffect(Dispatchers.IO) {
        updateState { copy(videoMetadataUiState = UiState.Loading) }

        val videoMetaUiState = streamRepository
            .getVideoMetadata(state.url)
            .fold(
                ifLeft = { UiState.Error(it.message) },
                ifRight = { VideoMetadataUiState.fromDTO(it).toUiState() },
            )

        updateState { copy(videoMetadataUiState = videoMetaUiState) }
    }
}
