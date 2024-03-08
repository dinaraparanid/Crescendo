package com.paranid5.crescendo.presentation.main.tracks.views.bar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.tracks.properties.compose.collectTrackOrderAsState
import com.paranid5.crescendo.presentation.ui.extensions.toString
import com.paranid5.crescendo.presentation.ui.LocalAppColors

@Composable
fun TrackOrderLabel(
    modifier: Modifier = Modifier,
    viewModel: TracksViewModel = koinActivityViewModel()
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current
    val trackOrder by viewModel.collectTrackOrderAsState()

    Text(
        text = trackOrder.toString(context),
        color = colors.primary,
        fontSize = 14.sp,
        modifier = modifier
    )
}