package com.paranid5.crescendo.presentation.main.trimmer.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.paranid5.crescendo.presentation.composition_locals.trimmer.LocalTrimmerFocusPoints
import com.paranid5.crescendo.presentation.composition_locals.trimmer.LocalTrimmerPositionBroadcast
import com.paranid5.crescendo.presentation.main.trimmer.FocusPoints
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.presentation.main.trimmer.views.waveform.TrimmedDuration
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun TrimmerScreenContent(
    viewModel: TrimmerViewModel,
    shownEffectsState: MutableIntState,
    modifier: Modifier = Modifier
) {
    val isFileSaveDialogShownState = remember {
        mutableStateOf(false)
    }

    Box(modifier) {
        TrimmerScreenContentOriented(
            viewModel = viewModel,
            shownEffectsState = shownEffectsState,
            isFileSaveDialogShownState = isFileSaveDialogShownState
        )

        FileSaveDialog(
            viewModel = viewModel,
            isDialogShownState = isFileSaveDialogShownState,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TrimmerScreenContentOriented(
    viewModel: TrimmerViewModel,
    shownEffectsState: MutableIntState,
    isFileSaveDialogShownState: MutableState<Boolean>
) {
    val config = LocalConfiguration.current

    val (startBorder, playback, endBorder) = remember {
        FocusRequester.createRefs()
    }

    val focusPoints = remember(startBorder, playback, endBorder) {
        FocusPoints(startBorder, playback, endBorder)
    }

    val positionBroadcast = remember {
        MutableSharedFlow<Long>()
    }

    CompositionLocalProvider(
        LocalTrimmerFocusPoints provides focusPoints,
        LocalTrimmerPositionBroadcast provides positionBroadcast
    ) {
        when (config.orientation) {
            Configuration.ORIENTATION_LANDSCAPE ->
                TrimmerScreenContentLandscape(
                    viewModel = viewModel,
                    shownEffectsState = shownEffectsState,
                    isFileSaveDialogShownState = isFileSaveDialogShownState,
                    modifier = Modifier.fillMaxSize()
                )

            else -> TrimmerScreenContentPortrait(
                viewModel = viewModel,
                shownEffectsState = shownEffectsState,
                isFileSaveDialogShownState = isFileSaveDialogShownState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun TrimmerScreenContentPortrait(
    viewModel: TrimmerViewModel,
    shownEffectsState: MutableIntState,
    isFileSaveDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) = ConstraintLayout(modifier) {
    val (
        titleArtist,
        waveform,
        effects,
        duration,
        playbackButtons,
        controllers,
        saveButton
    ) = createRefs()

    TitleArtistColumn(
        viewModel = viewModel,
        modifier = Modifier.constrainAs(titleArtist) {
            start.linkTo(parent.start, margin = 16.dp)
            end.linkTo(parent.end, margin = 16.dp)
            centerHorizontallyTo(parent)
        }
    )

    WaveformWithPosition(
        viewModel = viewModel,
        spikeWidthRatio = WAVEFORM_SPIKE_WIDTH_RATIO,
        modifier = Modifier.constrainAs(waveform) {
            top.linkTo(titleArtist.bottom, margin = 24.dp)
            bottom.linkTo(effects.top, margin = 8.dp)
            centerHorizontallyTo(parent)
            height = Dimension.fillToConstraints
        },
    )

    EffectsButtons(
        shownEffectsState = shownEffectsState,
        modifier = Modifier.constrainAs(effects) {
            bottom.linkTo(playbackButtons.top, margin = 16.dp)
            start.linkTo(parent.start, margin = 8.dp)
        }
    )

    TrimmedDuration(
        viewModel = viewModel,
        modifier = Modifier.constrainAs(duration) {
            top.linkTo(effects.top)
            bottom.linkTo(effects.bottom)
            centerHorizontallyTo(parent)
        }
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
private fun TrimmerScreenContentLandscape(
    viewModel: TrimmerViewModel,
    shownEffectsState: MutableIntState,
    isFileSaveDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) = ConstraintLayout(modifier) {
    val (
        titleArtist,
        waveform,
        effects,
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
            top.linkTo(titleArtist.bottom, margin = 4.dp)
            bottom.linkTo(playbackButtons.top, margin = 2.dp)
            centerHorizontallyTo(parent)
            height = Dimension.fillToConstraints
        }
    )

    EffectsButtons(
        shownEffectsState = shownEffectsState,
        modifier = Modifier.constrainAs(effects) {
            bottom.linkTo(controllers.top, margin = 4.dp)
            start.linkTo(parent.start, margin = 8.dp)
        }
    )

    TrimmedDuration(
        viewModel = viewModel,
        modifier = Modifier.constrainAs(duration) {
            top.linkTo(effects.top)
            bottom.linkTo(effects.bottom)
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