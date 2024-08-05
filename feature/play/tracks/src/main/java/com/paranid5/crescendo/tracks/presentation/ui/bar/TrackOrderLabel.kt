package com.paranid5.crescendo.tracks.presentation.ui.bar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.ui.foundation.AppBarCardLabel
import com.paranid5.crescendo.utils.extensions.toString

@Composable
internal fun TrackOrderLabel(
    trackOrder: TrackOrder,
    modifier: Modifier = Modifier,
) = AppBarCardLabel(
    text = trackOrder.toString(LocalContext.current),
    modifier = modifier,
)
