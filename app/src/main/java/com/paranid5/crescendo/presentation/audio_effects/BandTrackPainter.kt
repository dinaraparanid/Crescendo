package com.paranid5.crescendo.presentation.audio_effects

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toDrawable
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.ui.utils.CoilUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "BandTrackPainter"

@Composable
fun getBandTrackModel(width: Int, height: Int): ImageRequest {
    val context = LocalContext.current
    val coilUtils = CoilUtils(context)

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    Log.d(TAG, "Band width: $width height: $height")

    LaunchedEffect(width, height) {
        coverModel = withContext(Dispatchers.IO) {
            coilUtils.getBitmapFromResourceCatching(
                res = R.drawable.audio_track_horizontal_night_transparent,
                size = width to height
            )
                .map { it.toDrawable(context.resources) }
                .getOrThrow()
        }
    }

    return ImageRequest.Builder(LocalContext.current)
        .data(coverModel)
        .size(width, height)
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .build()
}

@Composable
fun rememberBandTrackPainter(width: Int, height: Int, ) =
    rememberAsyncImagePainter(model = getBandTrackModel(width, height))