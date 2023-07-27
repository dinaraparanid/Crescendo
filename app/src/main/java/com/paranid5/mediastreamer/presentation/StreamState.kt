package com.paranid5.mediastreamer.presentation

enum class StreamStates { SEARCHING, STREAMING }

inline val StreamStates.screen
    get() = when (this) {
        StreamStates.SEARCHING -> Screens.StreamFetching
        StreamStates.STREAMING -> Screens.Audio.Playing
    }

inline val StreamStates.nextState
    get() = when (this) {
        StreamStates.SEARCHING -> StreamStates.STREAMING
        StreamStates.STREAMING -> StreamStates.SEARCHING
    }