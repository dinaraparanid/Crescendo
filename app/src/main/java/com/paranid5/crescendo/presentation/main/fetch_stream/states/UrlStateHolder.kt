package com.paranid5.crescendo.presentation.main.fetch_stream.states

import androidx.lifecycle.SavedStateHandle
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentUrlFlow

private const val CURRENT_TEXT = "current_text"

class UrlStateHolder(
    private val savedStateHandle: SavedStateHandle,
    private val storageHandler: StorageHandler
) {
    val currentTextStateByVM by lazy {
        savedStateHandle.getStateFlow<String?>(CURRENT_TEXT, initialValue = null)
    }

    val currentTextFlowByStorage by lazy {
        storageHandler.currentUrlFlow
    }

    fun setCurrentText(currentText: String) {
        savedStateHandle[CURRENT_TEXT] = currentText
    }
}