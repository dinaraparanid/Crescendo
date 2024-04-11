package com.paranid5.crescendo.tracks.presentation.views.bar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.tracks.presentation.TracksViewModel
import com.paranid5.crescendo.tracks.presentation.properties.compose.collectTrackOrderAsState
import com.paranid5.crescendo.utils.extensions.toString
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun TrackOrderLabel(
    modifier: Modifier = Modifier,
    viewModel: TracksViewModel = koinViewModel()
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