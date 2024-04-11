package com.paranid5.crescendo.fetch_stream.data

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.StateFlow

private const val CURRENT_TEXT = "current_text"

internal interface UrlDataSource {
    val savedUrlState: StateFlow<String?>
    fun setCurrentText(currentText: String)
}

internal class UrlDataSourceImpl(private val savedStateHandle: SavedStateHandle) : UrlDataSource {
    override val savedUrlState by lazy {
        savedStateHandle.getStateFlow<String?>(CURRENT_TEXT, initialValue = null)
    }

    override fun setCurrentText(currentText: String) {
        savedStateHandle[CURRENT_TEXT] = currentText
    }
}