package com.paranid5.crescendo.core.media.images

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap

@Deprecated("Will be removed")
fun Drawable.toResizedBitmap(size: ImageSize? = null) =
    when (size) {
        null -> toBitmap()
        else -> toBitmap(size.width, size.height)
    }