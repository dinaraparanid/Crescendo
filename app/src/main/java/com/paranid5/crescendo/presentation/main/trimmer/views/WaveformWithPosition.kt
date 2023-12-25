package com.paranid5.crescendo.presentation.main.trimmer.views

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.presentation.composition_locals.trimmer.LocalTrimmerWaveformScrollState
import com.paranid5.crescendo.presentation.main.trimmer.PLAYBACK_CIRCLE_CENTER
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackAlphaFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackControllerOffsetFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackTextFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.waveformWidthFlow
import com.paranid5.crescendo.presentation.main.trimmer.views.waveform.TrimWaveform
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.textWidth

@Composable
fun WaveformWithPosition(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
    spaceBetween: Dp = 2.dp
) {
    val waveformScrollState = LocalTrimmerWaveformScrollState.current!!

    val playbackTextOffsetAnim = animatePlaybackTextOffsetAsState(
        viewModel = viewModel,
        spikeWidthRatio = spikeWidthRatio
    )

    Column(modifier.horizontalScroll(waveformScrollState)) {
        TrimWaveform(
            viewModel = viewModel,
            modifier = Modifier
                .weight(1F)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(spaceBetween))

        PlaybackPositionText(
            viewModel = viewModel,
            modifier = Modifier.offset(x = playbackTextOffsetAnim.dp)
        )
    }
}

@Composable
private fun PlaybackPositionText(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel
) {
    val colors = LocalAppColors.current
    val playbackText by viewModel.playbackTextFlow.collectAsState(initial = "")
    val playbackAlpha by viewModel.playbackAlphaFlow.collectAsState(initial = 0F)

    Text(
        text = playbackText,
        color = colors.fontColor,
        fontSize = 10.sp,
        modifier = modifier.alpha(playbackAlpha)
    )
}

@Composable
private fun animatePlaybackTextOffsetAsState(
    viewModel: TrimmerViewModel,
    spikeWidthRatio: Int
): Int {
    val playbackControllerOffset by viewModel
        .playbackControllerOffsetFlow(spikeWidthRatio)
        .collectAsState(initial = 0F)

    val waveformWidth by viewModel
        .waveformWidthFlow(spikeWidthRatio)
        .collectAsState(initial = 0)

    val playbackText by viewModel.playbackTextFlow.collectAsState(initial = "")

    val playbackTextWidth = textWidth(
        text = playbackText,
        style = TextStyle(fontSize = 10.sp)
    )

    val playbackTextOffset by remember(
        playbackControllerOffset,
        playbackTextWidth,
        waveformWidth
    ) {
        derivedStateOf {
            playbackTextOffset(
                playbackControllerOffset = playbackControllerOffset,
                playbackTextWidth = playbackTextWidth,
                waveformWidth = waveformWidth
            )
        }
    }

    val playbackTextOffsetAnim by animateIntAsState(
        targetValue = playbackTextOffset.toInt(), label = ""
    )

    return playbackTextOffsetAnim
}

private fun playbackTextOffset(
    playbackControllerOffset: Float,
    playbackTextWidth: Float,
    waveformWidth: Int
): Float {
    val playbackTextHalfWidth = playbackTextWidth / 2

    return when {
        playbackControllerOffset < playbackTextHalfWidth -> 0F

        playbackControllerOffset + playbackTextHalfWidth + PLAYBACK_CIRCLE_CENTER > waveformWidth ->
            waveformWidth.toFloat() - playbackTextWidth - PLAYBACK_CIRCLE_CENTER

        else -> playbackControllerOffset - playbackTextHalfWidth
    }
}