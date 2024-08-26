package com.paranid5.crescendo.trimmer.presentation.composition_locals

import androidx.compose.runtime.compositionLocalOf
import com.paranid5.crescendo.trimmer.domain.entities.FocusPoints

internal val LocalTrimmerFocusPoints = compositionLocalOf<FocusPoints?> { null }