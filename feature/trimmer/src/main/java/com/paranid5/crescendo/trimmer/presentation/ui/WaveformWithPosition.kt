package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.presentation.PLAYBACK_CIRCLE_CENTER
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerWaveformScrollState
import com.paranid5.crescendo.trimmer.presentation.ui.waveform.TrimWaveform
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.ui.foundation.AppText
import com.paranid5.crescendo.ui.utils.textWidth

@Composable
internal fun WaveformWithPosition(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
    spaceBetween: Dp = dimensions.padding.minimum,
) = nullable {
    val waveformScrollState = LocalTrimmerWaveformScrollState.current.bind()

    val playbackTextOffsetAnim = animatePlaybackTextOffsetAsState(
        state = state,
        spikeWidthRatio = spikeWidthRatio,
    )

    Column(modifier.horizontalScroll(waveformScrollState)) {
        TrimWaveform(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier
                .weight(1F)
                .align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(spaceBetween))

        PlaybackPositionText(
            state = state,
            modifier = Modifier.offset(x = playbackTextOffsetAnim.dp),
        )
    }
}

@Composable
private fun PlaybackPositionText(
    state: TrimmerState,
    modifier: Modifier = Modifier,
) {
    val playbackText by remember(state) {
        derivedStateOf { state.playbackPositions.playbackText }
    }

    val playbackAlpha by remember(state) {
        derivedStateOf { state.playbackProperties.playbackAlpha }
    }

    AppText(
        text = playbackText,
        style = typography.captionSm.copy(
            color = colors.text.primary,
        ),
        modifier = modifier.alpha(playbackAlpha),
    )
}

@Composable
private fun animatePlaybackTextOffsetAsState(
    state: TrimmerState,
    spikeWidthRatio: Int,
): Int {
    val playbackControllerOffset by remember(state, spikeWidthRatio) {
        derivedStateOf { state.playbackControllerOffset(spikeWidthRatio) }
    }

    val waveformWidth by remember(state, spikeWidthRatio) {
        derivedStateOf { state.waveformWidth(spikeWidthRatio) }
    }

    val playbackText by remember(state) {
        derivedStateOf { state.playbackPositions.playbackText }
    }

    val playbackTextWidth = textWidth(
        text = playbackText,
        style = typography.captionSm,
    )

    val playbackTextOffset by remember(
        playbackControllerOffset,
        playbackTextWidth,
        waveformWidth,
    ) {
        derivedStateOf {
            playbackTextOffset(
                playbackControllerOffset = playbackControllerOffset,
                playbackTextWidth = playbackTextWidth,
                waveformWidth = waveformWidth,
            )
        }
    }

    val playbackTextOffsetAnim by animateIntAsState(
        targetValue = playbackTextOffset.toInt(), label = "",
    )

    return playbackTextOffsetAnim
}

private fun playbackTextOffset(
    playbackControllerOffset: Float,
    playbackTextWidth: Float,
    waveformWidth: Int,
): Float {
    val playbackTextHalfWidth = playbackTextWidth / 2F

    return when {
        playbackControllerOffset < playbackTextHalfWidth -> 0F

        playbackControllerOffset + playbackTextHalfWidth + PLAYBACK_CIRCLE_CENTER > waveformWidth ->
            waveformWidth.toFloat() - playbackTextWidth - PLAYBACK_CIRCLE_CENTER

        else -> playbackControllerOffset - playbackTextHalfWidth
    }
}
