package com.paranid5.crescendo.trimmer.presentation.ui.waveform

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
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

    Text(
        text = trimmedDuration,
        style = typography.body,
        fontWeight = FontWeight.Bold,
        color = colors.text.onHighContrast,
        modifier = modifier,
    )
}