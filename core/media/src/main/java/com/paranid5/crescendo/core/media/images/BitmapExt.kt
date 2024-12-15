package com.paranid5.crescendo.core.media.images

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.palette.graphics.Palette

@Deprecated("Will be removed")
inline val Bitmap.withPalette
    get() = Palette.from(this).generate() to this

@Deprecated("Will be removed")
fun Bitmap.toBitmapDrawable(context: Context) =
    toDrawable(context.resources)