package com.paranid5.crescendo.trimmer.presentation.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerFocusPoints
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerPositionBroadcast
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerWaveformScrollState
import com.paranid5.crescendo.trimmer.domain.entities.FocusPoints
import com.paranid5.crescendo.trimmer.presentation.views.waveform.TrimmedDuration
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
internal fun TrimmerScreenContent(modifier: Modifier = Modifier) {
    val isFileSaveDialogShownState = remember {
        mutableStateOf(false)
    }

    val screenWidthPxState = remember {
        mutableIntStateOf(1)
    }

    var screenWidthPx by screenWidthPxState

    Box(modifier.onGloballyPositioned { screenWidthPx = it.size.width }) {
        TrimmerScreenContentOriented(
            screenWidthPxState = screenWidthPxState,
            isFileSaveDialogShownState = isFileSaveDialogShownState
        )

        FileSaveDialog(
            isDialogShownState = isFileSaveDialogShownState,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TrimmerScreenContentOriented(
    screenWidthPxState: MutableIntState,
    isFileSaveDialogShownState: MutableState<Boolean>
) {
    val config = LocalConfiguration.current

    val (startBorder, playback, endBorder) = remember {
        FocusRequester.createRefs()
    }

    val focusPoints = remember(startBorder, playback, endBorder) {
        FocusPoints(startBorder, playback, endBorder)
    }

    val positionBroadcast = remember { MutableSharedFlow<Long>() }
    val waveformScrollState = rememberScrollState()

    CompositionLocalProvider(
        LocalTrimmerFocusPoints provides focusPoints,
        LocalTrimmerPositionBroadcast provides positionBroadcast,
        LocalTrimmerWaveformScrollState provides waveformScrollState
    ) {
        when (config.orientation) {
            Configuration.ORIENTATION_LANDSCAPE ->
                TrimmerScreenContentLandscape(
                    screenWidthPxState = screenWidthPxState,
                    isFileSaveDialogShownState = isFileSaveDialogShownState,
                    modifier = Modifier.fillMaxSize()
                )

            else -> TrimmerScreenContentPortrait(
                screenWidthPxState = screenWidthPxState,
                isFileSaveDialogShownState = isFileSaveDialogShownState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun TrimmerScreenContentPortrait(
    screenWidthPxState: MutableIntState,
    isFileSaveDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) = ConstraintLayout(modifier) {
    val (
        titleArtist,
        waveform,
        effects,
        duration,
        zoom,
        playbackButtons,
        controllers,
        saveButton
    ) = createRefs()

    TitleArtistColumn(
        Modifier.constrainAs(titleArtist) {
            start.linkTo(parent.start, margin = 16.dp)
            end.linkTo(parent.end, margin = 16.dp)
            centerHorizontallyTo(parent)
        }
    )

    WaveformWithPosition(
        spikeWidthRatio = WAVEFORM_SPIKE_WIDTH_RATIO,
        modifier = Modifier.constrainAs(waveform) {
            top.linkTo(titleArtist.bottom, margin = 24.dp)
            bottom.linkTo(effects.top, margin = 8.dp)
            centerHorizontallyTo(parent)
            height = Dimension.fillToConstraints
        },
    )

    EffectsButtons(
        Modifier.constrainAs(effects) {
            bottom.linkTo(playbackButtons.top, margin = 16.dp)
            start.linkTo(parent.start, margin = 8.dp)
        }
    )

    TrimmedDuration(
        Modifier.constrainAs(duration) {
            top.linkTo(effects.top)
            bottom.linkTo(effects.bottom)
            centerHorizontallyTo(parent)
        }
    )

    ZoomControllers(
        screenWidthPxState = screenWidthPxState,
        modifier = Modifier.constrainAs(zoom) {
            top.linkTo(effects.top)
            bottom.linkTo(effects.bottom)
            end.linkTo(parent.end, margin = 8.dp)
        }
    )

    PlaybackButtons(
        Modifier.constrainAs(playbackButtons) {
            centerTo(parent)
        }
    )

    BorderControllers(
        Modifier.constrainAs(controllers) {
            top.linkTo(playbackButtons.bottom, margin = 32.dp)
            width = Dimension.matchParent
        }
    )

    FileSaveButton(
        isFileSaveDialogShownState = isFileSaveDialogShownState,
        textModifier = Modifier.padding(vertical = 4.dp),
        modifier = Modifier.constrainAs(saveButton) {
            top.linkTo(controllers.bottom, margin = 32.dp)
            start.linkTo(parent.start, margin = 16.dp)
            end.linkTo(parent.end, margin = 16.dp)
            width = Dimension.matchParent
        }
    )
}

@Composable
private fun TrimmerScreenContentLandscape(
    screenWidthPxState: MutableIntState,
    isFileSaveDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) = ConstraintLayout(modifier) {
    val (
        titleArtist,
        waveform,
        effects,
        duration,
        zoom,
        playbackButtons,
        controllers,
        saveButton
    ) = createRefs()

    TitleArtistColumn(
        spaceBetween = 2.dp,
        modifier = Modifier.constrainAs(titleArtist) {
            start.linkTo(parent.start)
            centerHorizontallyTo(parent)
        }
    )

    WaveformWithPosition(
        Modifier.constrainAs(waveform) {
            top.linkTo(titleArtist.bottom, margin = 4.dp)
            bottom.linkTo(playbackButtons.top, margin = 2.dp)
            centerHorizontallyTo(parent)
            height = Dimension.fillToConstraints
        }
    )

    EffectsButtons(
        modifier = Modifier.constrainAs(effects) {
            bottom.linkTo(controllers.top, margin = 4.dp)
            start.linkTo(parent.start, margin = 8.dp)
        }
    )

    TrimmedDuration(
        Modifier.constrainAs(duration) {
            top.linkTo(effects.top)
            bottom.linkTo(effects.bottom)
            centerHorizontallyTo(controllers)
        }
    )

    ZoomControllers(
        screenWidthPxState = screenWidthPxState,
        modifier = Modifier.constrainAs(zoom) {
            bottom.linkTo(playbackButtons.top, margin = 2.dp)
            centerHorizontallyTo(playbackButtons)
        }
    )

    BorderControllers(
        Modifier.constrainAs(controllers) {
            bottom.linkTo(parent.bottom, margin = 8.dp)
            start.linkTo(parent.start, margin = 8.dp)
            end.linkTo(playbackButtons.start, margin = 16.dp)
            width = Dimension.fillToConstraints
        }
    )

    PlaybackButtons(
        Modifier.constrainAs(playbackButtons) {
            bottom.linkTo(saveButton.top, margin = 2.dp)
            end.linkTo(parent.end, margin = 8.dp)
        }
    )

    FileSaveButton(
        isFileSaveDialogShownState = isFileSaveDialogShownState,
        modifier = Modifier.constrainAs(saveButton) {
            bottom.linkTo(parent.bottom, margin = 4.dp)
            start.linkTo(controllers.end, margin = 16.dp)
            end.linkTo(parent.end, margin = 8.dp)
            width = Dimension.fillToConstraints
        }
    )
}