package com.paranid5.crescendo.media.images

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
        getBitmapFromPathCatching(context, path, size, bitmapSettings)
            .getOrNull()
            ?: getThumbnailBitmap(context)
    }
}

fun getTrackCoverBitmapBlocking(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) =
    getBitmapFromPathBlockingCatching(context, path, size, bitmapSettings)
        .getOrNull()
        ?: getThumbnailBitmapBlocking(context)

suspend fun getTrackCoverBitmapWithPaletteAsync(
    context: Context,
    path: String,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        getBitmapFromPathWithPaletteCatching(context, path, size, bitmapSettings)
            .getOrNull()
            ?: getThumbnailBitmapWithPalette(context)
    }
}

fun getTrackCoverBitmapWithPaletteBlocking(
    context: Context,
    path: String,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) =
    getBitmapFromPathWithPaletteBlockingCatching(context, path, size, bitmapSettings)
        .getOrNull()
        ?: getThumbnailBitmapWithPaletteBlocking(context)

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
            ?: getThumbnailBitmapDrawable(context)
    }
}

fun getTrackCoverBlocking(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) =
    getBitmapFromPathBlockingCatching(context, path, size, bitmapSettings)
        .map { it.toBitmapDrawable(context) }
        .getOrNull()
        ?: getThumbnailBitmapDrawableBlocking(context)

suspend fun getTrackCoverWithPaletteAsync(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        getBitmapFromPathWithPaletteCatching(context, path, size, bitmapSettings)
            .map { (palette, bitmap) ->
                palette to bitmap.toBitmapDrawable(context)
            }
            .getOrNull()
            ?: getThumbnailBitmapDrawableWithPalette(context)
    }
}

fun getTrackCoverWithPaletteBlocking(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) =
    getBitmapFromPathWithPaletteBlockingCatching(context, path, size, bitmapSettings)
        .map { (palette, bitmap) ->
            palette to bitmap.toBitmapDrawable(context)
        }
        .getOrNull()
        ?: getThumbnailBitmapDrawableWithPaletteBlocking(context)