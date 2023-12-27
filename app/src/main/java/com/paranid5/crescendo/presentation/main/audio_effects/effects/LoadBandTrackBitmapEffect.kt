package com.paranid5.crescendo.presentation.main.audio_effects.effects

import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.R
import com.paranid5.crescendo.media.images.ImageSize
import com.paranid5.crescendo.media.images.getBitmapFromResourceCatching
import com.paranid5.crescendo.media.images.toBitmapDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LoadBandTrackBitmapEffect(
    width: Int,
    height: Int,
    coverModelState: MutableState<BitmapDrawable?>
) {
    val context = LocalContext.current
    var coverModel by coverModelState

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
}