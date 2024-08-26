package com.paranid5.crescendo.trimmer.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.domain.entities.FocusPoints
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerFocusPoints
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerWaveformScrollState
import com.paranid5.crescendo.trimmer.presentation.ui.waveform.TrimmedDuration
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

@Composable
internal fun TrimmerScreenContent(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFileSaveDialogShownState = remember {
        mutableStateOf(false)
    }

    val screenWidthPxState = remember {
        mutableIntStateOf(1)
    }

    var screenWidthPx by screenWidthPxState

    Box(modifier.onGloballyPositioned { screenWidthPx = it.size.width }) {
        TrimmerScreenContentOriented(
            state = state,
            onUiIntent = onUiIntent,
            screenWidthPx = screenWidthPx,
            isFileSaveDialogShownState = isFileSaveDialogShownState,
        )

        FileSaveDialog(
            state = state,
            isDialogShownState = isFileSaveDialogShownState,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TrimmerScreenContentOriented(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    screenWidthPx: Int,
    isFileSaveDialogShownState: MutableState<Boolean>,
) {
    val config = LocalConfiguration.current

    val (startBorder, playback, endBorder) = remember {
        FocusRequester.createRefs()
    }

    val focusPoints = remember(startBorder, playback, endBorder) {
        FocusPoints(startBorder, playback, endBorder)
    }

    val waveformScrollState = rememberScrollState()

    CompositionLocalProvider(
        LocalTrimmerFocusPoints provides focusPoints,
        LocalTrimmerWaveformScrollState provides waveformScrollState,
    ) {
        when (config.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> TrimmerScreenContentLandscape(
                state = state,
                onUiIntent = onUiIntent,
                screenWidthPx = screenWidthPx,
                isFileSaveDialogShownState = isFileSaveDialogShownState,
                modifier = Modifier.fillMaxSize(),
            )

            else -> TrimmerScreenContentPortrait(
                state = state,
                onUiIntent = onUiIntent,
                screenWidthPx = screenWidthPx,
                isFileSaveDialogShownState = isFileSaveDialogShownState,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun TrimmerScreenContentPortrait(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    screenWidthPx: Int,
    isFileSaveDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) = ConstraintLayout(modifier) {
    val (
        titleArtist,
        waveform,
        effects,
        duration,
        zoom,
        playbackButtons,
        controllers,
        saveButton,
    ) = createRefs()

    val appPadding = dimensions.padding

    TitleArtistColumn(
        state = state,
        modifier = Modifier.constrainAs(titleArtist) {
            start.linkTo(parent.start, margin = appPadding.extraMedium)
            end.linkTo(parent.end, margin = appPadding.extraMedium)
            centerHorizontallyTo(parent)
        },
    )

    WaveformWithPosition(
        state = state,
        onUiIntent = onUiIntent,
        spikeWidthRatio = WAVEFORM_SPIKE_WIDTH_RATIO,
        modifier = Modifier.constrainAs(waveform) {
            top.linkTo(titleArtist.bottom, margin = appPadding.extraBig)
            bottom.linkTo(effects.top, margin = appPadding.small)
            centerHorizontallyTo(parent)
            height = Dimension.fillToConstraints
        },
    )

    EffectsButtons(
        onUiIntent = onUiIntent,
        modifier = Modifier.constrainAs(effects) {
            bottom.linkTo(playbackButtons.top, margin = appPadding.extraMedium)
            start.linkTo(parent.start, margin = appPadding.small)
        },
    )

    TrimmedDuration(
        state = state,
        modifier = Modifier.constrainAs(duration) {
            top.linkTo(effects.top)
            bottom.linkTo(effects.bottom)
            centerHorizontallyTo(parent)
        },
    )

    ZoomControllers(
        state = state,
        onUiIntent = onUiIntent,
        screenWidthPx = screenWidthPx,
        modifier = Modifier.constrainAs(zoom) {
            top.linkTo(effects.top)
            bottom.linkTo(effects.bottom)
            end.linkTo(parent.end, margin = appPadding.small)
        },
    )

    PlaybackButtons(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.constrainAs(playbackButtons) {
            centerTo(parent)
        },
    )

    BorderControllers(
        state = state,
        onUiIntent = onUiIntent,
        Modifier.constrainAs(controllers) {
            top.linkTo(playbackButtons.bottom, margin = appPadding.large)
            width = Dimension.matchParent
        },
    )

    FileSaveButton(
        state = state,
        isFileSaveDialogShownState = isFileSaveDialogShownState,
        textModifier = Modifier.padding(vertical = appPadding.extraSmall),
        modifier = Modifier.constrainAs(saveButton) {
            top.linkTo(controllers.bottom, margin = appPadding.large)
            start.linkTo(parent.start, margin = appPadding.extraMedium)
            end.linkTo(parent.end, margin = appPadding.extraMedium)
            width = Dimension.matchParent
        },
    )
}

@Composable
private fun TrimmerScreenContentLandscape(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    screenWidthPx: Int,
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

    val appPadding = dimensions.padding

    TitleArtistColumn(
        state = state,
        verticalArrangement = Arrangement.spacedBy(appPadding.minimum),
        modifier = Modifier.constrainAs(titleArtist) {
            start.linkTo(parent.start)
            centerHorizontallyTo(parent)
        },
    )

    WaveformWithPosition(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.constrainAs(waveform) {
            top.linkTo(titleArtist.bottom, margin = appPadding.extraSmall)
            bottom.linkTo(playbackButtons.top, margin = appPadding.minimum)
            centerHorizontallyTo(parent)
            height = Dimension.fillToConstraints
        },
    )

    EffectsButtons(
        onUiIntent = onUiIntent,
        modifier = Modifier.constrainAs(effects) {
            bottom.linkTo(controllers.top, margin = appPadding.extraSmall)
            start.linkTo(parent.start, margin = appPadding.small)
        },
    )

    TrimmedDuration(
        state = state,
        modifier = Modifier.constrainAs(duration) {
            top.linkTo(effects.top)
            bottom.linkTo(effects.bottom)
            centerHorizontallyTo(controllers)
        },
    )

    ZoomControllers(
        state = state,
        onUiIntent = onUiIntent,
        screenWidthPx = screenWidthPx,
        modifier = Modifier.constrainAs(zoom) {
            bottom.linkTo(playbackButtons.top, margin = appPadding.minimum)
            centerHorizontallyTo(playbackButtons)
        },
    )

    BorderControllers(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.constrainAs(controllers) {
            bottom.linkTo(parent.bottom, margin = appPadding.small)
            start.linkTo(parent.start, margin = appPadding.small)
            end.linkTo(playbackButtons.start, margin = appPadding.extraMedium)
            width = Dimension.fillToConstraints
        },
    )

    PlaybackButtons(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.constrainAs(playbackButtons) {
            bottom.linkTo(saveButton.top, margin = appPadding.minimum)
            end.linkTo(parent.end, margin = appPadding.small)
        },
    )

    FileSaveButton(
        state = state,
        isFileSaveDialogShownState = isFileSaveDialogShownState,
        modifier = Modifier.constrainAs(saveButton) {
            bottom.linkTo(parent.bottom, margin = appPadding.extraSmall)
            start.linkTo(controllers.end, margin = appPadding.extraMedium)
            end.linkTo(parent.end, margin = appPadding.small)
            width = Dimension.fillToConstraints
        },
    )
}
