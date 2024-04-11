package com.paranid5.crescendo.trimmer.presentation.views.effects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectFadeInSecsAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectFadeOutAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectTrackDurationInMillisAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun FadeScreen(modifier: Modifier = Modifier) =
    Column(modifier) {
        FadeInController(Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        FadeOutController(Modifier.fillMaxWidth())
    }

@Composable
private fun FadeInController(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val fadeInSecs by viewModel.collectFadeInSecsAsState()
    val trackDurationMillis by viewModel.collectTrackDurationInMillisAsState()

    val trackDurationSecs by remember(trackDurationMillis) {
        derivedStateOf { trackDurationMillis / 1000 }
    }

    val maxFade by remember(trackDurationSecs) {
        derivedStateOf { minOf(trackDurationSecs / 2, 30) }
    }

    EffectController(
        label = fadeInLabel(fadeInSecs),
        iconPainter = painterResource(R.drawable.fade_in),
        valueState = fadeInSecs,
        minValue = 0L,
        maxValue = maxFade,
        steps = maxFade.toInt(),
        setEffect = { viewModel.setFadeInSecs(it.toLong()) },
        modifier = modifier
    )
}

@Composable
private fun fadeInLabel(fadeInSecs: Long) =
    "${stringResource(R.string.fade_in)}: ${fadeInSecs}s"

@Composable
private fun FadeOutController(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val fadeOutSecs by viewModel.collectFadeOutAsState()
    val trackDurationMillis by viewModel.collectTrackDurationInMillisAsState()

    val trackDurationSecs by remember(trackDurationMillis) {
        derivedStateOf { trackDurationMillis / 1000 }
    }

    val maxFade by remember(trackDurationSecs) {
        derivedStateOf { minOf(trackDurationSecs / 2, 30) }
    }

    EffectController(
        label = fadeOutLabel(fadeOutSecs),
        iconPainter = painterResource(R.drawable.fade_out),
        valueState = fadeOutSecs,
        minValue = 0L,
        maxValue = maxFade,
        steps = maxFade.toInt(),
        setEffect = { viewModel.setFadeOutSecs(it.toLong()) },
        modifier = modifier
    )
}

@Composable
private fun fadeOutLabel(fadeOutSecs: Long) =
    "${stringResource(R.string.fade_out)}: ${fadeOutSecs}s"