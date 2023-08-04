package com.paranid5.crescendo.presentation.composition_locals

import androidx.compose.runtime.staticCompositionLocalOf
import com.paranid5.crescendo.presentation.NavHostController

@JvmField
val LocalNavController = staticCompositionLocalOf(::NavHostController)