package com.paranid5.crescendo.domain.image.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.core.graphics.drawable.toDrawable
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.domain.image.model.BitmapWithPalette
import java.io.ByteArrayOutputStream

val Bitmap.withPalette
    get() = BitmapWithPalette(
        bitmap = this,
        palette = Palette.from(this).generate()
    )

fun Bitmap.toBitmapDrawable(context: Context) =
    toDrawable(context.resources)

val Bitmap.byteData: ByteArray
    get() = ByteArrayOutputStream().use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
        it.toByteArray()
    }

fun Bitmap.increaseDarkness(alpha: Int = 100) = Canvas(this).let { canvas ->
    canvas.drawARGB(alpha, 0, 0, 0)
    canvas.drawBitmap(this, Matrix(), Paint())
}

val Bitmap.safeConfig: Bitmap.Config
    get() = config ?: Bitmap.Config.ARGB_8888