package com.paranid5.crescendo.presentation.main.trimmer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.utils.extensions.timeString
import com.paranid5.crescendo.data.utils.extensions.toTimeOrNull
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.DefaultOutlinedTextField

@Composable
fun TrimmerScreen(viewModel: TrimmerViewModel, modifier: Modifier = Modifier) {
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
            modifier = Modifier.constrainAs(saveButton) {
                top.linkTo(controllers.bottom, margin = 32.dp)
                width = Dimension.matchParent
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TitleArtistColumn(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.value
    val track by viewModel.trackState.collectAsState()

    Column(modifier) {
        Text(
            text = track!!.title,
            fontSize = 20.sp,
            color = colors.inverseSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .basicMarquee(iterations = Int.MAX_VALUE)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = track!!.artist,
            fontSize = 16.sp,
            color = colors.inverseSurface,
            modifier = Modifier
                .basicMarquee(iterations = Int.MAX_VALUE)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun TrimmedDuration(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.value
    val duration by viewModel.trimmedDurationFlow.collectAsState(initial = 0L)

    Text(
        text = duration.timeString,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colors.primary,
        modifier = modifier
    )
}

@Composable
private fun PlaybackButtons(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.value
    val isPlaying by viewModel.isPlayingState.collectAsState()

    Row(modifier) {
        IconButton(
            onClick = {
                // TODO: skip to 10 secs back
            },
            modifier = Modifier
                .size(40.dp)
                .padding(4.dp)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.previous),
                contentDescription = stringResource(id = R.string.ten_secs_back),
                tint = colors.primary,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.width(16.dp))

        IconButton(
            onClick = {
                viewModel.setPlaying(!isPlaying)
                // TODO: start/stop playback
            },
            modifier = Modifier
                .size(48.dp)
                .padding(4.dp)
                .background(color = colors.secondary, shape = CircleShape)
                .align(Alignment.CenterVertically)
        ) {
            when {
                isPlaying -> Icon(
                    painter = painterResource(id = R.drawable.pause),
                    contentDescription = stringResource(id = R.string.pause),
                    tint = colors.background,
                    modifier = Modifier.fillMaxSize()
                )

                else -> Icon(
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = stringResource(id = R.string.play),
                    tint = colors.background,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        IconButton(
            onClick = {
                // TODO: skip to 10 secs forward
            },
            modifier = Modifier
                .size(40.dp)
                .padding(4.dp)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.next),
                contentDescription = stringResource(id = R.string.ten_secs_forward),
                tint = colors.primary,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun BorderControllers(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.value

    val startMillis by viewModel.startPosInMillisState.collectAsState()
    val endMillis by viewModel.endPosInMillisState.collectAsState()

    var startMillisStr by remember(startMillis) {
        mutableStateOf(startMillis.timeString)
    }

    var endMillisStr by remember(endMillis) {
        mutableStateOf(endMillis.timeString)
    }

    Row(modifier) {
        Spacer(Modifier.weight(1F))

        DefaultOutlinedTextField(
            value = startMillisStr,
            onValueChange = {
                startMillisStr = it.take(8)
                it.toTimeOrNull()?.let(viewModel::setStartPosInMillis)
            },
            label = {
                Text(
                    text = stringResource(R.string.start_time),
                    color = colors.primary,
                    fontSize = 12.sp,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
        )

        Spacer(Modifier.weight(1F))

        DefaultOutlinedTextField(
            value = endMillis.timeString,
            onValueChange = {
                endMillisStr = it.take(8)
                it.toTimeOrNull()?.let(viewModel::setEndPosInMillis)
            },
            label = {
                Text(
                    text = stringResource(R.string.end_time),
                    color = colors.primary,
                    fontSize = 12.sp,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
        )

        Spacer(Modifier.weight(1F))
    }
}

@Composable
private fun SaveButton(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.value

    Button(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.onBackground
        ),
        onClick = {
            // TODO: start trimming
        }
    ) {
        Text(
            text = stringResource(R.string.save),
            color = colors.inverseSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}