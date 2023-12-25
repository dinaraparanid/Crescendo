package com.paranid5.crescendo.presentation.composition_locals.trimmer

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableSharedFlow

val LocalTrimmerPositionBroadcast = staticCompositionLocalOf { MutableSharedFlow<Long>() }