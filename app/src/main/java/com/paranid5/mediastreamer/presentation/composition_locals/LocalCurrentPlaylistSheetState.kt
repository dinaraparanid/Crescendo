package com.paranid5.mediastreamer.presentation.composition_locals

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.compositionLocalOf

@OptIn(ExperimentalMaterialApi::class)
@JvmField
val LocalCurrentPlaylistSheetState = compositionLocalOf<ModalBottomSheetState?> { null }