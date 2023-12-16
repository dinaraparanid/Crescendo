package com.paranid5.crescendo.presentation.ui.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun ByteArray.toBitmap(): Bitmap =
    BitmapFactory.decodeByteArray(this, 0, size)