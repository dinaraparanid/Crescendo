package com.paranid5.crescendo.trimmer.presentation.ui.effects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackProperties.Companion.MaxPitch
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackProperties.Companion.MaxSpeed
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackProperties.Companion.MinPitch
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.PlaybackProperties.Companion.MinSpeed
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

@Composable
internal fun PitchSpeedScreen(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    PitchController(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(dimensions.padding.small))

    SpeedController(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun PitchController(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pitch = remember(state.playbackProperties.pitch) {
        state.playbackProperties.pitch
    }

    EffectController(
        modifier = modifier,
        label = pitchLabel(pitch),
        iconPainter = painterResource(R.drawable.pitch),
        valueState = pitch,
        minValue = MinPitch,
        maxValue = MaxPitch,
    ) {
        onUiIntent(TrimmerUiIntent.Player.UpdatePitch(pitch = it))
    }
}

@Composable
private fun pitchLabel(pitch: Float) =
    "${stringResource(R.string.pitch)}: ${String.format("%.2f", pitch)}"

@Composable
private fun SpeedController(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val speed = remember(state.playbackProperties.speed) {
        state.playbackProperties.speed
    }

    EffectController(
        modifier = modifier,
        label = speedLabel(speed),
        iconPainter = painterResource(R.drawable.speed),
        valueState = speed,
        minValue = MinSpeed,
        maxValue = MaxSpeed,
    ) {
        onUiIntent(TrimmerUiIntent.Player.UpdateSpeed(speed = it))
    }
}

@Composable
private fun speedLabel(speed: Float) =
    "${stringResource(R.string.speed)}: ${String.format("%.2f", speed)}"
