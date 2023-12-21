package com.paranid5.crescendo.presentation.main.trimmer.views.waveform

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.domain.utils.extensions.timeString
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.trimmedDurationFlow
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun TrimmedDuration(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val duration by viewModel.trimmedDurationFlow.collectAsState(initial = 0L)

    Text(
        text = duration.timeString,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colors.primary,
        modifier = modifier
    )
}