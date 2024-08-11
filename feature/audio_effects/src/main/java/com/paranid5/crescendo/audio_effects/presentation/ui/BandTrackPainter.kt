package com.paranid5.crescendo.audio_effects.presentation.ui

import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.audio_effects.presentation.effects.LoadBandTrackBitmapEffect

@Composable
internal fun getBandTrackModel(width: Int, height: Int): ImageRequest {
    val context = LocalContext.current

    val coverModelState = remember {
        mutableStateOf<BitmapDrawable?>(null)
    }

    val coverModel by coverModelState

    LoadBandTrackBitmapEffect(width, height, coverModelState)

    return ImageRequest.Builder(context)
        .data(coverModel)
        .size(width, height)
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .build()
}

@Composable
internal fun rememberBandTrackPainter(width: Int, height: Int) =
    rememberAsyncImagePainter(model = getBandTrackModel(width, height))
