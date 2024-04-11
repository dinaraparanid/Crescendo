package com.paranid5.crescendo.trimmer.presentation.composition_locals

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableSharedFlow

internal val LocalTrimmerPositionBroadcast = staticCompositionLocalOf { MutableSharedFlow<Long>() }