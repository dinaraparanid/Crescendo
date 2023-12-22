package com.paranid5.crescendo.presentation.main.trimmer

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.paranid5.crescendo.presentation.main.trimmer.views.BorderControllers
import com.paranid5.crescendo.presentation.main.trimmer.views.FileSaveButton
import com.paranid5.crescendo.presentation.main.trimmer.views.FileSaveDialog
import com.paranid5.crescendo.presentation.main.trimmer.views.TitleArtistColumn
import com.paranid5.crescendo.presentation.main.trimmer.views.WaveformWithDurations
import com.paranid5.crescendo.presentation.main.trimmer.views.WaveformWithPosition
import com.paranid5.crescendo.presentation.main.trimmer.views.playback.PlaybackButtons
import com.paranid5.crescendo.presentation.main.trimmer.views.waveform.TrimmedDuration

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

internal const val WAVEFORM_PADDING = CONTROLLER_CIRCLE_RADIUS +
        CONTROLLER_CIRCLE_CENTER / 2 +
        CONTROLLER_RECT_WIDTH

@Composable
fun TrimmerScreen(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val isFileSaveDialogShownState = remember {
        mutableStateOf(false)
    }

    Box(modifier) {
        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE ->
                TrimmerScreenLandscape(
                    viewModel = viewModel,
                    isFileSaveDialogShownState = isFileSaveDialogShownState,
                    modifier = Modifier.fillMaxSize()
                )

            else -> TrimmerScreenPortrait(
                viewModel = viewModel,
                isFileSaveDialogShownState = isFileSaveDialogShownState,
                modifier = Modifier.fillMaxSize()
            )
        }

        FileSaveDialog(
            viewModel = viewModel,
            isDialogShownState = isFileSaveDialogShownState,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun TrimmerScreenPortrait(
    viewModel: TrimmerViewModel,
    isFileSaveDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) = ConstraintLayout(modifier) {
    val (
        titleArtist,
        waveform,
        playbackButtons,
        controllers,
        saveButton
    ) = createRefs()

    TitleArtistColumn(
        viewModel = viewModel,
        modifier = Modifier.constrainAs(titleArtist) {
            start.linkTo(parent.start, margin = 16.dp)
            centerHorizontallyTo(parent)
        }
    )

    WaveformWithDurations(
        viewModel = viewModel,
        spikeWidthRatio = WAVEFORM_SPIKE_WIDTH_RATIO,
        modifier = Modifier.constrainAs(waveform) {
            top.linkTo(titleArtist.bottom, margin = 24.dp)
            bottom.linkTo(playbackButtons.top, margin = 16.dp)
            centerHorizontallyTo(parent)
            height = Dimension.fillToConstraints
        },
    )

    PlaybackButtons(
        viewModel = viewModel,
        modifier = Modifier.constrainAs(playbackButtons) { centerTo(parent) }
    )

    BorderControllers(
        viewModel = viewModel,
        modifier = Modifier.constrainAs(controllers) {
            top.linkTo(playbackButtons.bottom, margin = 32.dp)
            width = Dimension.matchParent
        }
    )

    FileSaveButton(
        viewModel = viewModel,
        isFileSaveDialogShownState = isFileSaveDialogShownState,
        textModifier = Modifier.padding(vertical = 4.dp),
        modifier = Modifier.constrainAs(saveButton) {
            top.linkTo(controllers.bottom, margin = 32.dp)
            width = Dimension.matchParent
        }
    )
}

@Composable
private fun TrimmerScreenLandscape(
    viewModel: TrimmerViewModel,
    isFileSaveDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) = ConstraintLayout(modifier) {
    val (
        titleArtist,
        waveform,
        duration,
        playbackButtons,
        controllers,
        saveButton
    ) = createRefs()

    TitleArtistColumn(
        viewModel = viewModel,
        spaceBetween = 2.dp,
        modifier = Modifier.constrainAs(titleArtist) {
            start.linkTo(parent.start)
            centerHorizontallyTo(parent)
        }
    )

    WaveformWithPosition(
        viewModel = viewModel,
        modifier = Modifier.constrainAs(waveform) {
            top.linkTo(titleArtist.bottom, margin = 8.dp)
            bottom.linkTo(playbackButtons.top, margin = 8.dp)
            centerHorizontallyTo(parent)
            height = Dimension.fillToConstraints
        }
    )

    TrimmedDuration(
        viewModel = viewModel,
        modifier = Modifier.constrainAs(duration) {
            bottom.linkTo(controllers.top, margin = 4.dp)
            centerHorizontallyTo(controllers)
        }
    )

    BorderControllers(
        viewModel = viewModel,
        modifier = Modifier.constrainAs(controllers) {
            bottom.linkTo(parent.bottom, margin = 8.dp)
            start.linkTo(parent.start, margin = 8.dp)
            end.linkTo(playbackButtons.start, margin = 16.dp)
            width = Dimension.fillToConstraints
        }
    )

    PlaybackButtons(
        viewModel = viewModel,
        modifier = Modifier.constrainAs(playbackButtons) {
            bottom.linkTo(saveButton.top, margin = 4.dp)
            end.linkTo(parent.end, margin = 8.dp)
        }
    )

    FileSaveButton(
        viewModel = viewModel,
        isFileSaveDialogShownState = isFileSaveDialogShownState,
        modifier = Modifier.constrainAs(saveButton) {
            bottom.linkTo(parent.bottom, margin = 4.dp)
            start.linkTo(controllers.end, margin = 16.dp)
            end.linkTo(parent.end, margin = 8.dp)
            width = Dimension.fillToConstraints
        }
    )
}