package com.paranid5.crescendo.feature.stream.fetch.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state

internal class FetchStreamViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
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
    }
}
