package com.paranid5.crescendo.core.media.images

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend inline fun getVideoCoverBitmapOrThumbnailAsync(
    context: Context,
    videoCovers: List<String>,
    size: ImageSize? = null,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        videoCovers
            .map { getBitmapFromUrlCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.getOrNull()
            ?: getThumbnailBitmap(context)
    }
}

suspend inline fun getVideoCoverBitmapAsync(
    context: Context,
    videoCovers: List<String>,
    size: ImageSize? = null,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        videoCovers
            .map { getBitmapFromUrlCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.getOrNull()
    }
}

suspend inline fun getVideoCoverAsync(
    context: Context,
    videoCovers: List<String>,
    size: ImageSize? = null,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        videoCovers
            .map { getBitmapFromUrlCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.map { it.toBitmapDrawable(context) }
            ?.getOrNull()
            ?: getThumbnailBitmapDrawable(context)
    }
}

suspend inline fun getVideoCoverWithPaletteAsync(
    context: Context,
    videoCovers: List<String>,
    size: ImageSize? = null,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        videoCovers
            .map { getBitmapFromUrlWithPaletteCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.map { (palette, bitmap) ->
                palette to bitmap.toBitmapDrawable(context)
            }
            ?.getOrNull()
            ?: getThumbnailBitmapDrawableWithPalette(context)
    }
}
