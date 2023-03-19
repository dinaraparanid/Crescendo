package com.paranid5.mediastreamer.presentation.search_stream

import androidx.lifecycle.SavedStateHandle
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.ObservableViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SearchStreamViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<SearchStreamPresenter, SearchStreamUIHandler>() {
    private companion object {
        private const val CURRENT_TEXT = "current_text"
    }

    private val storageHandler by inject<StorageHandler>()

    override val presenter by inject<SearchStreamPresenter> {
        val savedByStateHandle = savedStateHandle
            .getStateFlow<String?>(CURRENT_TEXT, null)
            .value

        val savedByStorageHandler = storageHandler.currentUrlState.value
        parametersOf(savedByStateHandle ?: savedByStorageHandler)
    }

    override val handler by inject<SearchStreamUIHandler>()

    private val _isConfirmButtonPressedState = MutableStateFlow(false)
    val isConfirmButtonPressedState = _isConfirmButtonPressedState.asStateFlow()

    fun onConfirmUrlButtonPressed() {
        _isConfirmButtonPressedState.value = true
    }

    fun finishUrlSetting() {
        _isConfirmButtonPressedState.value = false
    }
}