package com.paranid5.crescendo.feature.play.main.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state

internal class PlayViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), PlayViewModel, StatePublisher<PlayState> {
    companion object {
        private const val StateKey = "state"
    }

    override val stateFlow =
        savedStateHandle.getStateFlow(key = StateKey, initialValue = PlayState())

    override fun updateState(func: PlayState.() -> PlayState) {
        savedStateHandle[StateKey] = func(state)
    }

    override fun onUiIntent(intent: PlayUiIntent) {
        when (intent) {
            is PlayUiIntent.SearchCancelClick -> when {
                state.searchQuery.isEmpty() -> updateState { copy(isSearchActive = false) }
                else -> updateState { copy(searchQuery = "") }
            }

            is PlayUiIntent.UpdatePagerState ->
                updateState { copy(pagerState = intent.pagerState) }

            is PlayUiIntent.UpdateSearchQuery ->
                updateState { copy(searchQuery = intent.query, isSearchActive = true) }

            is PlayUiIntent.ClearBackResult ->
                updateState { copy(backResult = null) }
        }
    }
}
