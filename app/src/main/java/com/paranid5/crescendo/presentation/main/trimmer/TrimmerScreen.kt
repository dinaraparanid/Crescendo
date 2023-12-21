package com.paranid5.crescendo.presentation.main.trimmer

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.paranid5.crescendo.domain.utils.extensions.timeString
import com.paranid5.crescendo.presentation.ui.extensions.pxToDp
import com.paranid5.crescendo.presentation.ui.extensions.safeDiv
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

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
) = when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> TrimmerScreenLandscape(viewModel, modifier)
    else -> TrimmerScreenPortrait(viewModel, modifier)
}

@Composable
private fun TrimmerScreenPortrait(viewModel: TrimmerViewModel, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    val track by viewModel.trackState.collectAsState()
    val durationInMillis by remember { derivedStateOf { track!!.duration } }

    val playbackPosition by viewModel.playbackPositionState.collectAsState()
    val playbackText by remember { derivedStateOf { playbackPosition.timeString } }
    val playbackOffset by remember { derivedStateOf { playbackPosition safeDiv durationInMillis } }
    val canvasWidth by remember { derivedStateOf { durationInMillis / 1000 * WAVEFORM_SPIKE_WIDTH_RATIO } }

    val isPlaying by viewModel.isPlayingState.collectAsState()
    val playbackTimeAlpha by animateFloatAsState(if (isPlaying) 1F else 0F, label = "")

    val waveformScrollState = rememberScrollState()
    val playbackTextMeasurer = rememberTextMeasurer()

    val playbackPositionMeasure = playbackTextMeasurer.measure(
        text = playbackText,
        style = TextStyle(fontSize = 10.sp)
    )

    val playbackTextWidthPx by remember {
        derivedStateOf { playbackPositionMeasure.size.width }
    }

    val playbackTextWidth = playbackTextWidthPx.pxToDp().value

    val playbackTextHalfWidth by remember {
        derivedStateOf { playbackTextWidth / 2 }
    }

    val playbackControllerOffset by remember {
        derivedStateOf {
            CONTROLLER_CIRCLE_CENTER / 2 +
                    playbackOffset * (canvasWidth - CONTROLLER_CIRCLE_RADIUS - CONTROLLER_RECT_OFFSET) +
                    CONTROLLER_RECT_OFFSET
        }
    }

    val playbackTextOffset by remember {
        derivedStateOf {
            when {
                playbackControllerOffset < playbackTextHalfWidth -> 0F

                playbackControllerOffset + playbackTextHalfWidth + PLAYBACK_CIRCLE_CENTER > canvasWidth ->
                    canvasWidth.toFloat() - playbackTextWidth - PLAYBACK_CIRCLE_CENTER

                else -> playbackControllerOffset - playbackTextHalfWidth
            }
        }
    }

    val playbackTextOffsetAnim by animateIntAsState(
        targetValue = playbackTextOffset.toInt(), label = ""
    )

    ConstraintLayout(modifier) {
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

        Column(
            Modifier.constrainAs(waveform) {
                top.linkTo(titleArtist.bottom, margin = 24.dp)
                bottom.linkTo(playbackButtons.top, margin = 16.dp)
                centerHorizontallyTo(parent)
                height = Dimension.fillToConstraints
            }
        ) {
            Column(
                Modifier
                    .weight(1F)
                    .align(Alignment.CenterHorizontally)
                    .horizontalScroll(waveformScrollState)
            ) {
                TrimWaveform(
                    model = track!!.path,
                    durationInMillis = track!!.duration,
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = playbackText,
                    color = colors.fontColor,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .alpha(playbackTimeAlpha)
                        .offset(x = playbackTextOffsetAnim.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            TrimmedDuration(
                viewModel = viewModel,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

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

        SaveButton(
            viewModel = viewModel,
            textModifier = Modifier.padding(vertical = 4.dp),
            modifier = Modifier.constrainAs(saveButton) {
                top.linkTo(controllers.bottom, margin = 32.dp)
                width = Dimension.matchParent
            }
        )
    }
}

@Composable
private fun TrimmerScreenLandscape(viewModel: TrimmerViewModel, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    val track by viewModel.trackState.collectAsState()
    val durationInMillis by remember { derivedStateOf { track!!.duration } }

    val playbackPosition by viewModel.playbackPositionState.collectAsState()
    val playbackText by remember { derivedStateOf { playbackPosition.timeString } }
    val playbackOffset by remember { derivedStateOf { playbackPosition safeDiv durationInMillis } }
    val canvasWidth by remember { derivedStateOf { durationInMillis / 1000 * WAVEFORM_SPIKE_WIDTH_RATIO } }

    val isPlaying by viewModel.isPlayingState.collectAsState()
    val playbackTimeAlpha by animateFloatAsState(if (isPlaying) 1F else 0F, label = "")

    val waveformScrollState = rememberScrollState()
    val playbackTextMeasurer = rememberTextMeasurer()

    val playbackPositionMeasure = playbackTextMeasurer.measure(
        text = playbackText,
        style = TextStyle(fontSize = 10.sp)
    )

    val playbackTextWidthPx by remember {
        derivedStateOf { playbackPositionMeasure.size.width }
    }

    val playbackTextWidth = playbackTextWidthPx.pxToDp().value

    val playbackTextHalfWidth by remember {
        derivedStateOf { playbackTextWidth / 2 }
    }

    val playbackControllerOffset by remember {
        derivedStateOf {
            CONTROLLER_CIRCLE_CENTER / 2 +
                    playbackOffset * (canvasWidth - CONTROLLER_CIRCLE_RADIUS - CONTROLLER_RECT_OFFSET) +
                    CONTROLLER_RECT_OFFSET
        }
    }

    val playbackTextOffset by remember {
        derivedStateOf {
            when {
                playbackControllerOffset < playbackTextHalfWidth -> 0F

                playbackControllerOffset + playbackTextHalfWidth + PLAYBACK_CIRCLE_CENTER > canvasWidth ->
                    canvasWidth.toFloat() - playbackTextWidth - PLAYBACK_CIRCLE_CENTER

                else -> playbackControllerOffset - playbackTextHalfWidth
            }
        }
    }

    val playbackTextOffsetAnim by animateIntAsState(
        targetValue = playbackTextOffset.toInt(), label = ""
    )

    ConstraintLayout(modifier) {
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

        Column(
            Modifier
                .horizontalScroll(waveformScrollState)
                .constrainAs(waveform) {
                    top.linkTo(titleArtist.bottom, margin = 8.dp)
                    bottom.linkTo(playbackButtons.top, margin = 8.dp)
                    centerHorizontallyTo(parent)
                    height = Dimension.fillToConstraints
                }
        ) {
            TrimWaveform(
                model = track!!.path,
                durationInMillis = track!!.duration,
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1F)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = playbackText,
                color = colors.fontColor,
                fontSize = 10.sp,
                modifier = Modifier
                    .alpha(playbackTimeAlpha)
                    .offset(x = playbackTextOffsetAnim.dp)
            )
        }

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

        SaveButton(
            viewModel = viewModel,
            modifier = Modifier.constrainAs(saveButton) {
                bottom.linkTo(parent.bottom, margin = 4.dp)
                start.linkTo(controllers.end, margin = 16.dp)
                end.linkTo(parent.end, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )
    }
}