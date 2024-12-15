package com.paranid5.crescendo.audio_effects.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.core.resources.R

@Composable
internal fun getBandTrackModel(width: Int, height: Int): ImageRequest {
    val context = LocalContext.current

    return remember(context, width, height) {
        ImageRequest.Builder(context)
            .data(R.drawable.audio_track)
            .size(width = width, height = height)
            .precision(Precision.EXACT)
            .scale(Scale.FIT)
            .build()
    }
}

@Composable
internal fun rememberBandTrackPainter(width: Int, height: Int) =
    rememberAsyncImagePainter(model = getBandTrackModel(width, height))
