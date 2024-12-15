package com.paranid5.crescendo.domain.image.utils

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.paranid5.crescendo.domain.image.model.ImageSize

fun Drawable.toResizedBitmap(size: ImageSize? = null) =
    when (size) {
        null -> toBitmap()
        else -> toBitmap(size.width, size.height)
    }
