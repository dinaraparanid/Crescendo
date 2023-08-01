package com.paranid5.mediastreamer.presentation.audio_effects

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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.ui.utils.GlideUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "BandTrackPainter"

@Composable
internal fun rememberBandTrackPainter(
    width: Int,
    height: Int,
): AsyncImagePainter {
    val context = LocalContext.current
    val glideUtils = GlideUtils(context)

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    Log.d(TAG, "Band width: $width height: $height")

    LaunchedEffect(width, height) {
        coverModel = withContext(Dispatchers.IO) {
            glideUtils.getBitmapFromResourceCatching(
                res = R.drawable.audio_track_horizontal_night_transparent,
                size = width to height
            )
                .map { it.toDrawable(context.resources) }
                .getOrThrow()
        }
    }

    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(coverModel)
            .size(width, height)
            .precision(Precision.EXACT)
            .scale(Scale.FILL)
            .build()
    )
}