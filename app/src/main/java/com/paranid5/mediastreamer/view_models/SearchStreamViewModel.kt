package com.paranid5.mediastreamer.view_models

import androidx.lifecycle.SavedStateHandle
import com.paranid5.mediastreamer.presenters.SearchStreamPresenter
import com.paranid5.mediastreamer.ui_handlers.SearchStreamUIHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SearchStreamViewModel(savedStateHandle: SavedStateHandle) :
    ObservableViewModel<SearchStreamPresenter, SearchStreamUIHandler>() {
    private companion object {
        private const val CURRENT_TEXT = "current_text"
    }

    override val presenter by inject<SearchStreamPresenter> {
        parametersOf(savedStateHandle.getStateFlow<String?>(CURRENT_TEXT, null).value)
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