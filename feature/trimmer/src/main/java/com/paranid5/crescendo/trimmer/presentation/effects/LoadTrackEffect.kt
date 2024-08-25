package com.paranid5.crescendo.trimmer.presentation.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

@Composable
internal fun LoadTrackEffect(
    trackPath: String,
    onUiIntent: (TrimmerUiIntent) -> Unit,
) = LaunchedEffect(trackPath) {
    onUiIntent(TrimmerUiIntent.LoadTrack(trackPath = trackPath))
}
