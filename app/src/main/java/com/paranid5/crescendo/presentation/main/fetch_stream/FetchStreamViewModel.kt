package com.paranid5.crescendo.presentation.main.fetch_stream

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.domain.StorageHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

class FetchStreamViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val storageHandler: StorageHandler,
) : ViewModel() {
    private companion object {
        private const val CURRENT_TEXT = "current_text"
    }

    private val _currentTextState by lazy {
        val savedByStateHandle = savedStateHandle.get<String>(CURRENT_TEXT)
        val savedByStorageHandler = storageHandler.currentUrlState.value
        MutableStateFlow(savedByStateHandle ?: savedByStorageHandler)
    }

    val currentTextState by lazy { _currentTextState.asStateFlow() }

    fun setCurrentText(currentText: String) {
        savedStateHandle[CURRENT_TEXT] = _currentTextState.updateAndGet { currentText }
    }
}