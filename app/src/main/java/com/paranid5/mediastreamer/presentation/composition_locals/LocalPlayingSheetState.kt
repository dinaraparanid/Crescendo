package com.paranid5.mediastreamer.presentation.composition_locals

import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.compositionLocalOf

@OptIn(ExperimentalMaterialApi::class)
@JvmField
val LocalPlayingSheetState = compositionLocalOf<BottomSheetScaffoldState?> { null }