package com.paranid5.crescendo.core.media.images

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun getTrackCoverBitmapAsync(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        getBitmapFromPathCatching(context, path, size, bitmapSettings).getOrNull()
    }
}

suspend fun getTrackCoverAsync(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        getBitmapFromPathCatching(context, path, size, bitmapSettings)
            .map { it.toBitmapDrawable(context) }
            .getOrNull()
    }
}

suspend fun getTrackCoverWithPaletteAsync(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        getBitmapFromPathWithPaletteCatching(context, path, size, bitmapSettings)
            .map { (palette, bitmap) -> palette to bitmap.toBitmapDrawable(context) }
            .getOrNull()
            .let { it?.first to it?.second }
    }
}
