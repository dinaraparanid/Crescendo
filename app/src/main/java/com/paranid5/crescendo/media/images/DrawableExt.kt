package com.paranid5.crescendo.media.images

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap

fun Drawable.toResizedBitmap(size: ImageSize? = null) =
    when (size) {
        null -> toBitmap()
        else -> toBitmap(size.width, size.height)
    }