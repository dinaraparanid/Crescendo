package com.paranid5.crescendo.trimmer.data

import androidx.compose.ui.focus.FocusState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal interface FocusEventDataSource {
    val focusEventState: StateFlow<FocusState?>

    fun setFocusEvent(event: FocusState)
}

internal class FocusEventDataSourceImpl : FocusEventDataSource {
    private val _focusEventState by lazy {
        MutableStateFlow<FocusState?>(null)
    }

    override val focusEventState by lazy {
        _focusEventState.asStateFlow()
    }

    override fun setFocusEvent(event: FocusState) = _focusEventState.update { event }
}