package com.paranid5.crescendo.presentation.main_activity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.presentation.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

class MainActivityViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private companion object {
        private const val CUR_SCREEN_STATE = "cur_screen"
        private const val SCREENS_STACK_STATE = "screens_stack"
    }

    private val _curScreenState by lazy {
        MutableStateFlow(savedStateHandle.get<Screens>(CUR_SCREEN_STATE) ?: Screens.Tracks)
    }

    val curScreenState by lazy { _curScreenState.asStateFlow() }

    fun setCurScreen(screen: Screens) {
        savedStateHandle[CUR_SCREEN_STATE] = _curScreenState.updateAndGet { screen }
    }

    private val _screensStackState by lazy {
        MutableStateFlow(savedStateHandle[SCREENS_STACK_STATE] ?: mutableListOf<Screens>())
    }

    val screensStackState by lazy { _screensStackState.asStateFlow() }

    fun setScreensStack(screensStack: MutableList<Screens>) {
        savedStateHandle[SCREENS_STACK_STATE] = _screensStackState.updateAndGet { screensStack }
    }
}