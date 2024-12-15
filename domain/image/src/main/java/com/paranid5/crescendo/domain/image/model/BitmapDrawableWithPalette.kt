package com.paranid5.crescendo.domain.image.model

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.domain.image.utils.toBitmapDrawable

data class BitmapDrawableWithPalette(val drawable: BitmapDrawable, val palette: Palette) {
    companion object {
        fun fromBitmapWithPalette(context: Context, bitmapWithPalette: BitmapWithPalette) =
            BitmapDrawableWithPalette(
                drawable = bitmapWithPalette.bitmap.toBitmapDrawable(context),
                palette = bitmapWithPalette.palette,
            )
    }
}
