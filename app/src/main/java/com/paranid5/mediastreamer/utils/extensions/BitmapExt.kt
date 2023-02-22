package com.paranid5.mediastreamer.utils.extensions

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

inline val Bitmap.byteData: ByteArray
    get() = ByteArrayOutputStream().use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
        it.toByteArray()
    }