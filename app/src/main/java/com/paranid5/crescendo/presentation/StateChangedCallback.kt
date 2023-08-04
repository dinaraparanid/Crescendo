package com.paranid5.crescendo.presentation

import kotlinx.coroutines.flow.StateFlow

data class StateChangedCallback<H : UIHandler>(
    val uiHandler: H,
    val state: StateFlow<Boolean>,
    val onDispose: (H.() -> Unit)? = null,
    val callback: suspend H.() -> Unit,
) {
    suspend inline operator fun invoke() {
        state.collect { isChanged -> if (isChanged) callback(uiHandler) }
    }
}