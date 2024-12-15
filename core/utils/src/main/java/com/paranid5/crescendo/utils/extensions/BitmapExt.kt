package com.paranid5.crescendo.utils.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import java.io.ByteArrayOutputStream

@Deprecated("Will be removed")
inline val Bitmap.byteData: ByteArray
    get() = ByteArrayOutputStream().use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
        it.toByteArray()
    }

@Deprecated("Will be removed")
fun Bitmap.increaseDarkness(alpha: Int = 100) = Canvas(this).let { canvas ->
    canvas.drawARGB(alpha, 0, 0, 0)
    canvas.drawBitmap(this, Matrix(), Paint())
}

@Deprecated("Will be removed")
inline val Bitmap.safeConfig: Bitmap.Config
    get() = config ?: Bitmap.Config.ARGB_8888