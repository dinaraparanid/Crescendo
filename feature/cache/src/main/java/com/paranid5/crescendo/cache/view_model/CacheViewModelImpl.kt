package com.paranid5.crescendo.cache.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.common.udf.StatePublisher
import com.paranid5.crescendo.core.common.udf.state
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.system.services.video_cache.VideoCacheServiceAccessor
import com.paranid5.crescendo.utils.extensions.sideEffect

internal class CacheViewModelImpl(
    private val savedStateHandle: SavedStateHandle,
    private val streamRepository: StreamRepository,
    private val videoCacheServiceAccessor: VideoCacheServiceAccessor,
) : ViewModel(), CacheViewModel, StatePublisher<CacheState> {
    companion object {
        private const val StateKey = "state"
    }

    override val stateFlow = savedStateHandle.getStateFlow(StateKey, CacheState())

    override fun updateState(func: CacheState.() -> CacheState) {
        savedStateHandle[StateKey] = func(state)
    }

    override fun onUiIntent(intent: CacheUiIntent) = when (intent) {
        is CacheUiIntent.UpdateDownloadUrl -> updateState { copy(downloadUrl = intent.url) }

        is CacheUiIntent.UpdateFilename -> updateState { copy(filename = intent.filename) }

        is CacheUiIntent.UpdateSelectedSaveOptionIndex -> updateState {
            copy(selectedSaveOptionIndex = intent.selectedSaveOptionIndex)
        }

        is CacheUiIntent.StartCaching -> startCaching()
    }

    private fun startCaching() = viewModelScope.sideEffect {
        streamRepository.updateDownloadingUrl(state.downloadUrl)

        videoCacheServiceAccessor.startCachingOrAddToQueue(
            videoUrl = state.downloadUrl,
            desiredFilename = state.filename,
            format = state.cacheFormat,
            trimRange = state.trimRange,
        )
    }
}
