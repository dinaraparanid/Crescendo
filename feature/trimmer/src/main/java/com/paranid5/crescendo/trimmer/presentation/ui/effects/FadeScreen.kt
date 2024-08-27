package com.paranid5.crescendo.trimmer.presentation.ui.effects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackPositions.Companion.MaxFade
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackPositions.Companion.MinFade
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

@Composable
internal fun FadeScreen(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    FadeInController(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(dimensions.padding.small))

    FadeOutController(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun FadeInController(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fadeInSecs = remember(state.playbackPositions.fadeInSecs) {
        state.playbackPositions.fadeInSecs
    }

    val trackDurationSecs = remember(state.trackDurationInSeconds) {
        state.trackDurationInSeconds
    }

    val maxFade by remember(trackDurationSecs) {
        derivedStateOf { minOf(trackDurationSecs / 2, MaxFade) }
    }

    EffectController(
        modifier = modifier,
        label = fadeInLabel(fadeInSecs),
        icon = ImageVector.vectorResource(R.drawable.ic_fade_in),
        valueState = fadeInSecs.toFloat(),
        minValue = MinFade.toFloat(),
        maxValue = maxFade.toFloat(),
        steps = maxFade.toInt(),
        setEffect = {
            onUiIntent(TrimmerUiIntent.Positions.UpdateFadeIn(position = it.toLong()))
        },
    )
}

@Composable
private fun fadeInLabel(fadeInSecs: Long) =
    "${stringResource(R.string.fade_in)}: ${fadeInSecs}s"

@Composable
private fun FadeOutController(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fadeOutSecs = remember(state.playbackPositions.fadeOutSecs) {
        state.playbackPositions.fadeOutSecs
    }

    val trackDurationSecs = remember(state.trackDurationInSeconds) {
        state.trackDurationInSeconds
    }

    val maxFade by remember(trackDurationSecs) {
        derivedStateOf { minOf(trackDurationSecs / 2, MaxFade) }
    }

    EffectController(
        modifier = modifier,
        label = fadeOutLabel(fadeOutSecs),
        icon = ImageVector.vectorResource(R.drawable.ic_fade_out),
        valueState = fadeOutSecs.toFloat(),
        minValue = MinFade.toFloat(),
        maxValue = maxFade.toFloat(),
        steps = maxFade.toInt(),
        setEffect = {
            onUiIntent(TrimmerUiIntent.Positions.UpdateFadeOut(position = it.toLong()))
        },
    )
}

@Composable
private fun fadeOutLabel(fadeOutSecs: Long) =
    "${stringResource(R.string.fade_out)}: ${fadeOutSecs}s"
