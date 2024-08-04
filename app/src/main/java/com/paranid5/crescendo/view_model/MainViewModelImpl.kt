package com.paranid5.crescendo.view_model

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.github.GitHubApi
import com.paranid5.crescendo.domain.web.OpenBrowserUseCase
import com.paranid5.crescendo.presentation.entity.ReleaseUiState
import com.paranid5.crescendo.ui.foundation.toUiState
import kotlinx.coroutines.launch

@Immutable
internal class MainViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val openBrowserUseCase: OpenBrowserUseCase,
    private val gitHubApi: GitHubApi,
) : ViewModel(), MainViewModel, StatePublisher<MainState> {
    companion object {
        private const val StateKey = "state"
    }

    override val stateFlow =
        savedStateHandle.getStateFlow(key = StateKey, initialValue = MainState())

    init {
        checkForUpdates()
    }

    override fun updateState(func: MainState.() -> MainState) {
        savedStateHandle[StateKey] = func(state)
    }

    override fun onUiIntent(intent: MainUiIntent) {
        when (intent) {
            is MainUiIntent.OpenVersionPage ->
                openBrowserUseCase.openBrowser(url = intent.url)

            is MainUiIntent.DismissVersionDialog ->
                updateState { copy(isUpdateDialogShown = false) }
        }
    }

    private fun checkForUpdates() = viewModelScope.launch {
        gitHubApi.checkForUpdates()?.let {
            val releaseState = ReleaseUiState.fromResponse(it).toUiState()
            updateState { copy(isUpdateDialogShown = true, releaseState = releaseState) }
        }
    }
}
