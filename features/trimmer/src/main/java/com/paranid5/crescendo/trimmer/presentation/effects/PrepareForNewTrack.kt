package com.paranid5.crescendo.trimmer.presentation.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.setTrackAndResetPositions
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PrepareForNewTrack(
    track: Track,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    LaunchedEffect(track) {
        viewModel.setTrackAndResetPositions(track)
    }
}