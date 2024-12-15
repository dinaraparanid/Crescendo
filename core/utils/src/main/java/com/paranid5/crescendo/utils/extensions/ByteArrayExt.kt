package com.paranid5.crescendo.utils.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory

@Deprecated("Will be removed")
fun ByteArray.toBitmap(): Bitmap =
    BitmapFactory.decodeByteArray(this, 0, size)