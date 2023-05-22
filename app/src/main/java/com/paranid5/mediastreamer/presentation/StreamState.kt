package com.paranid5.mediastreamer.presentation

enum class StreamStates { SEARCHING, STREAMING }

inline val StreamStates.screen
    get() = when (this) {
        StreamStates.SEARCHING -> Screens.MainScreens.Searching
        StreamStates.STREAMING -> Screens.MainScreens.StreamScreens.Streaming
    }

inline val StreamStates.nextState
    get() = when (this) {
        StreamStates.SEARCHING -> StreamStates.STREAMING
        StreamStates.STREAMING -> StreamStates.SEARCHING
    }