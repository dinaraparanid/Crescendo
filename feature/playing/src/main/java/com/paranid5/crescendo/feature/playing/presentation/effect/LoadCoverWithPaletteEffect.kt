package com.paranid5.crescendo.feature.playing.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.domain.image.model.BitmapDrawableWithPalette
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingViewModel
import com.paranid5.crescendo.utils.doNothing
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull

@NonRestartableComposable
@Composable
internal fun LoadCoverWithPaletteEffect(
    viewModel: PlayingViewModel,
    state: PlayingState,
    screenPlaybackStatus: PlaybackStatus,
    onResult: (BitmapDrawableWithPalette) -> Unit,
) {
    val trackCoverPath = state.currentTrackCoverPath
    val videoCovers = state.videoCovers

    LaunchedEffect(screenPlaybackStatus, trackCoverPath) {
        when (screenPlaybackStatus) {
            PlaybackStatus.PLAYING if trackCoverPath != null -> {
                viewModel
                    .retrieveBitmapDrawableFromMediaWithPalette(path = trackCoverPath,)
                    ?.let(onResult)
            }

            PlaybackStatus.STREAMING -> {
                videoCovers
                    .asFlow()
                    .mapNotNull(viewModel::downloadBitmapDrawableWithPalette)
                    .firstOrNull()
                    ?.let(onResult)
            }

            else -> doNothing
        }
    }
}
