package com.paranid5.crescendo.trimmer.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.tracks.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

internal const val MIN_SPIKE_HEIGHT = 1F
internal const val DEFAULT_GRAPHICS_LAYER_ALPHA = 0.99F

internal const val CONTROLLER_RECT_WIDTH = 15F
internal const val CONTROLLER_RECT_OFFSET = 7F

internal const val CONTROLLER_CIRCLE_RADIUS = 25F
internal const val CONTROLLER_CIRCLE_CENTER = 16F
internal const val CONTROLLER_HEIGHT_OFFSET = CONTROLLER_CIRCLE_RADIUS + CONTROLLER_CIRCLE_CENTER

internal const val CONTROLLER_ARROW_CORNER_BACK_OFFSET = 8F
internal const val CONTROLLER_ARROW_CORNER_FRONT_OFFSET = 10F
internal const val CONTROLLER_ARROW_CORNER_OFFSET = 12F

internal const val PLAYBACK_RECT_WIDTH = 5F
internal const val PLAYBACK_RECT_OFFSET = 2F

internal const val PLAYBACK_CIRCLE_RADIUS = 12F
internal const val PLAYBACK_CIRCLE_CENTER = 8F

internal const val WAVEFORM_SPIKE_WIDTH_RATIO = 5

internal const val WAVEFORM_PADDING =
    (CONTROLLER_CIRCLE_RADIUS +
            CONTROLLER_CIRCLE_CENTER / 2 +
            CONTROLLER_RECT_WIDTH).toInt()

private const val TrackPathKey = "trackPath"

@Composable
fun TrimmerScreen(
    backStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier,
    tracksRepository: TracksRepository = koinInject(),
) {
    var track by remember { mutableStateOf<Track?>(null) }

    val trackPath by remember(backStackEntry) {
        derivedStateOf { backStackEntry.arguments?.getString(TrackPathKey) }
    }

    LaunchedEffect(trackPath) {
        track = withContext(Dispatchers.IO) {
            trackPath?.let { tracksRepository.getTrackFromMediaStore(it) }
        }
    }

    track?.let {
        TrimmerScreenImpl(
            track = it,
            modifier = modifier
        )
    }
}