package com.paranid5.crescendo.audio_effects.presentation.effects

import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.media.images.getBitmapFromResourceCatching
import com.paranid5.crescendo.core.media.images.toBitmapDrawable
import com.paranid5.crescendo.core.resources.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun LoadBandTrackBitmapEffect(
    width: Int,
    height: Int,
    coverModelState: MutableState<BitmapDrawable?>,
) {
    val context = LocalContext.current
    var coverModel by coverModelState

    LaunchedEffect(width, height, context) {
        coverModel = withContext(Dispatchers.IO) {
            getBitmapFromResourceCatching(
                context = context,
                res = R.drawable.audio_track_horizontal_night_transparent,
                size = ImageSize(width, height),
            )
                .map { it.toBitmapDrawable(context) }
                .getOrNull()
        }
    }
}
