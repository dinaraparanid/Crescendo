package com.paranid5.crescendo.presentation.main.audio_effects

import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.R
import com.paranid5.crescendo.media.images.ImageSize
import com.paranid5.crescendo.media.images.getBitmapFromResourceCatching
import com.paranid5.crescendo.media.images.toBitmapDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun getBandTrackModel(width: Int, height: Int): ImageRequest {
    val context = LocalContext.current
    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }

    LaunchedEffect(width, height) {
        coverModel = withContext(Dispatchers.IO) {
            getBitmapFromResourceCatching(
                context = context,
                res = R.drawable.audio_track_horizontal_night_transparent,
                size = ImageSize(width, height)
            )
                .map { it.toBitmapDrawable(context) }
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
fun rememberBandTrackPainter(width: Int, height: Int) =
    rememberAsyncImagePainter(model = getBandTrackModel(width, height))