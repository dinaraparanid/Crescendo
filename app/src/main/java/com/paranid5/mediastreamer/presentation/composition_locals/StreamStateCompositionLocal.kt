package com.paranid5.mediastreamer.presentation.composition_locals

import androidx.compose.runtime.staticCompositionLocalOf
import com.paranid5.mediastreamer.presentation.ui.screens.Screens

enum class StreamStates { SEARCHING, STREAMING }

@JvmInline
value class StreamState(val value: StreamStates = StreamStates.SEARCHING)

@JvmField
val LocalStreamState = staticCompositionLocalOf { StreamState() }

inline val StreamState.screen
    get() = when (value) {
        StreamStates.SEARCHING -> Screens.StreamScreen.Searching
        StreamStates.STREAMING -> Screens.StreamScreen.Streaming
    }