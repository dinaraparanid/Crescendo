package com.paranid5.crescendo.trimmer.presentation.views

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.presentation.PLAYBACK_CIRCLE_CENTER
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerWaveformScrollState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectPlaybackAlphaAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectPlaybackTextAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectWaveformWidthAsState
import com.paranid5.crescendo.trimmer.presentation.properties.playbackControllerOffsetFlow
import com.paranid5.crescendo.trimmer.presentation.views.waveform.TrimWaveform
import com.paranid5.crescendo.ui.utils.textWidth
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WaveformWithPosition(
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
    spaceBetween: Dp = dimensions.padding.minimum,
) {
    val waveformScrollState = LocalTrimmerWaveformScrollState.current!!
    val playbackTextOffsetAnim = animatePlaybackTextOffsetAsState(spikeWidthRatio)

    Column(modifier.horizontalScroll(waveformScrollState)) {
        TrimWaveform(
            Modifier
                .weight(1F)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(spaceBetween))

        PlaybackPositionText(Modifier.offset(x = playbackTextOffsetAnim.dp))
    }
}

@Composable
private fun PlaybackPositionText(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val playbackText by viewModel.collectPlaybackTextAsState()
    val playbackAlpha by viewModel.collectPlaybackAlphaAsState()

    Text(
        text = playbackText,
        color = colors.text.primary,
        style = typography.captionSm,
        modifier = modifier.alpha(playbackAlpha)
    )
}

@Composable
private fun animatePlaybackTextOffsetAsState(
    spikeWidthRatio: Int,
    viewModel: TrimmerViewModel = koinViewModel(),
): Int {
    val playbackControllerOffset by viewModel
        .playbackControllerOffsetFlow(spikeWidthRatio)
        .collectLatestAsState(initial = 0F)

    val waveformWidth by viewModel.collectWaveformWidthAsState(spikeWidthRatio)
    val playbackText by viewModel.collectPlaybackTextAsState()

    val playbackTextWidth = textWidth(
        text = playbackText,
        style = typography.captionSm,
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