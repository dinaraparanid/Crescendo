package com.paranid5.crescendo.ui.composition_locals.playing

import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.compositionLocalOf

@OptIn(ExperimentalMaterialApi::class)
val LocalPlayingSheetState = compositionLocalOf<BottomSheetScaffoldState?> { null }