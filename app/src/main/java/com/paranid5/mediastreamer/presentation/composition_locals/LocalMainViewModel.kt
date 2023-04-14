package com.paranid5.mediastreamer.presentation.composition_locals

import androidx.compose.runtime.staticCompositionLocalOf
import com.paranid5.mediastreamer.presentation.main_activity.MainActivityViewModel

@JvmField
val LocalMainViewModel = staticCompositionLocalOf<MainActivityViewModel?> { null }