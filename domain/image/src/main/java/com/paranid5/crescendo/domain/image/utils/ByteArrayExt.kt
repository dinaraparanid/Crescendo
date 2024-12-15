package com.paranid5.crescendo.domain.image.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.paranid5.crescendo.domain.image.model.ImageSize

fun ByteArray.toBitmap(imageSize: ImageSize? = null): Bitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, size)

    return imageSize
        ?.let { Bitmap.createScaledBitmap(bitmap, it.width, it.height, false) }
        ?: bitmap
}
