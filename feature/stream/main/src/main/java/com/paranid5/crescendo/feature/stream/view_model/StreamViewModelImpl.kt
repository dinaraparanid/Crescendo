package com.paranid5.crescendo.feature.stream.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state

internal class StreamViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), StreamViewModel, StatePublisher<StreamState> {
    companion object {
        private const val StateKey = "state"
    }

    override val stateFlow = savedStateHandle.getStateFlow(StateKey, StreamState())

    override fun updateState(func: StreamState.() -> StreamState) {
        savedStateHandle[StateKey] = func(state)
    }

    override fun onUiIntent(intent: StreamUiIntent) = when (intent) {
        is StreamUiIntent.UpdatePagerState -> updateState { copy(pagerState = intent.pagerState) }
    }
}
