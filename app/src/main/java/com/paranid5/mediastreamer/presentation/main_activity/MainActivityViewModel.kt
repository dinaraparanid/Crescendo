package com.paranid5.mediastreamer.presentation.main_activity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.StreamStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainActivityViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private companion object {
        private const val CUR_SCREEN_STATE = "cur_screen"
        private const val SCREENS_STACK_STATE = "screens_stack"
    }

    val curScreenState = MutableStateFlow(
        savedStateHandle.getStateFlow<Screens>(
            CUR_SCREEN_STATE,
            Screens.MainScreens.Searching
        ).value
    )

    val streamScreenState = curScreenState
        .map {
            when (it) {
                Screens.MainScreens.StreamScreens.Streaming -> StreamStates.STREAMING
                else -> StreamStates.SEARCHING
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, StreamStates.SEARCHING)

    val screensStack = MutableStateFlow(
        savedStateHandle.getStateFlow(
            SCREENS_STACK_STATE,
            mutableListOf<Screens>()
        ).value
    )
}