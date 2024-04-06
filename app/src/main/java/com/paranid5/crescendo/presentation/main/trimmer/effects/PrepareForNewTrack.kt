package com.paranid5.crescendo.presentation.main.trimmer.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.setTrackAndResetPositions
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