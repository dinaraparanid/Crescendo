package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.domain.utils.extensions.timeString
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectSpeedAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectTrimmedDurationInMillisAsState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun TrimmedDuration(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    val duration by viewModel.collectTrimmedDurationInMillisAsState()
    val speed by viewModel.collectSpeedAsState()

    val trimmedDuration by remember(duration, speed) {
        derivedStateOf { (duration / speed).toLong() }
    }

    Text(
        text = trimmedDuration.timeString,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colors.primary,
        modifier = modifier
    )
}