package com.paranid5.crescendo.presentation.main.trimmer.views.effects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.pitchState
import com.paranid5.crescendo.presentation.main.trimmer.properties.setPitch
import com.paranid5.crescendo.presentation.main.trimmer.properties.setSpeed
import com.paranid5.crescendo.presentation.main.trimmer.properties.speedState

@Composable
fun PitchSpeedScreen(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) = Column(modifier) {
    PitchController(viewModel, Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    SpeedController(viewModel, Modifier.fillMaxWidth())
}

@Composable
private fun PitchController(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val pitch by viewModel.pitchState.collectAsState()

    EffectController(
        label = pitchLabel(pitch),
        iconPainter = painterResource(R.drawable.pitch),
        valueState = pitch,
        minValue = 0.5F,
        maxValue = 2F,
        setEffect = viewModel::setPitch,
        modifier = modifier
    )
}

@Composable
private fun pitchLabel(pitch: Float) =
    "${stringResource(R.string.pitch)}: ${String.format("%.2f", pitch)}"

@Composable
private fun SpeedController(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val speed by viewModel.speedState.collectAsState()

    EffectController(
        label = speedLabel(speed),
        iconPainter = painterResource(R.drawable.speed),
        valueState = speed,
        minValue = 0.5F,
        maxValue = 2F,
        setEffect = viewModel::setSpeed,
        modifier = modifier
    )
}

@Composable
private fun speedLabel(speed: Float) =
    "${stringResource(R.string.speed)}: ${String.format("%.2f", speed)}"