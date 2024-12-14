package com.paranid5.crescendo.trimmer.presentation.ui.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.ui.foundation.AppText
import com.paranid5.crescendo.utils.extensions.timeFormat

@Composable
internal fun TrimmedDuration(
    state: TrimmerState,
    modifier: Modifier = Modifier,
) {
    val duration = remember(state.playbackPositions.trimmedDurationInMillis) {
        state.playbackPositions.trimmedDurationInMillis
    }

    val speed = remember(state.playbackProperties.speed) {
        state.playbackProperties.speed
    }

    val trimmedDuration by remember(duration, speed) {
        derivedStateOf { (duration / speed).timeFormat }
    }

    AppText(
        text = trimmedDuration,
        modifier = modifier,
        style = typography.body.copy(
            fontWeight = FontWeight.W700,
            color = colors.text.onHighContrast,
        ),
    )
}