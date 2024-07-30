package com.paranid5.crescendo.trimmer.presentation.views.waveform

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectSpeedAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectTrimmedDurationInMillisAsState
import com.paranid5.crescendo.utils.extensions.timeString
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun TrimmedDuration(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val duration by viewModel.collectTrimmedDurationInMillisAsState()
    val speed by viewModel.collectSpeedAsState()

    val trimmedDuration by remember(duration, speed) {
        derivedStateOf { (duration / speed).toLong() }
    }

    Text(
        text = trimmedDuration.timeString,
        style = typography.body,
        fontWeight = FontWeight.Bold,
        color = colors.primary,
        modifier = modifier,
    )
}