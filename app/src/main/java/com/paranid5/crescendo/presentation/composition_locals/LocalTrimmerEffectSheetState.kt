package com.paranid5.crescendo.presentation.composition_locals

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.compositionLocalOf

@OptIn(ExperimentalMaterialApi::class)
val LocalTrimmerEffectSheetState = compositionLocalOf<ModalBottomSheetState?> { null }