package com.paranid5.crescendo.presentation.main.trimmer

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

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
    val track by viewModel.trackState.collectAsState()
    val waveformScrollState = rememberScrollState()

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
            Box(
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
                        .fillMaxHeight()
                        .align(Alignment.Center)
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
    val track by viewModel.trackState.collectAsState()
    val waveformScrollState = rememberScrollState()

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
            spacerHeight = 2.dp,
            modifier = Modifier.constrainAs(titleArtist) {
                start.linkTo(parent.start)
                centerHorizontallyTo(parent)
            }
        )

        Box(
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
                    .fillMaxHeight()
                    .align(Alignment.Center)
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