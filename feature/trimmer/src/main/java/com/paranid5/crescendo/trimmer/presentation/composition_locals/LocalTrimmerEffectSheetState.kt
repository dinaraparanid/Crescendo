package com.paranid5.crescendo.trimmer.presentation.composition_locals

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.compositionLocalOf

@OptIn(ExperimentalMaterialApi::class)
internal val LocalTrimmerEffectSheetState = compositionLocalOf<ModalBottomSheetState?> { null }
