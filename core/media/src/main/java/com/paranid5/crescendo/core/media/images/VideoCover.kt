package com.paranid5.crescendo.core.media.images

import android.content.Context
import android.graphics.Bitmap
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
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

inline fun getVideoCoverBitmapBlocking(
    context: Context,
    videoMetadata: VideoMetadata,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = videoMetadata
    .covers
    .map { getBitmapFromUrlBlockingCatching(context, it, size, bitmapSettings) }
    .firstOrNull { it.isRight() }
    ?.getOrNull()
    ?: getThumbnailBitmapBlocking(context)

suspend inline fun getVideoCoverBitmapWithPaletteAsync(
    context: Context,
    videoMetadata: VideoMetadata,
    size: ImageSize? = null,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        videoMetadata
            .covers
            .map { getBitmapFromUrlWithPaletteCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.getOrNull()
            ?: getThumbnailBitmapWithPalette(context)
    }
}

inline fun getVideoCoverBitmapWithPaletteBlocking(
    context: Context,
    videoMetadata: VideoMetadata,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = videoMetadata
    .covers
    .map { getBitmapFromUrlWithPaletteBlockingCatching(context, it, size, bitmapSettings) }
    .firstOrNull { it.isRight() }
    ?.getOrNull()
    ?: getThumbnailBitmapWithPaletteBlocking(context)

suspend inline fun getVideoCoverAsync(
    context: Context,
    videoMetadata: VideoMetadata,
    size: ImageSize? = null,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        videoMetadata
            .covers
            .map { getBitmapFromUrlCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.map { it.toBitmapDrawable(context) }
            ?.getOrNull()
            ?: getThumbnailBitmapDrawable(context)
    }
}

inline fun getVideoCoverBlocking(
    context: Context,
    videoMetadata: VideoMetadata,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = videoMetadata
    .covers
    .map { getBitmapFromUrlBlockingCatching(context, it, size, bitmapSettings) }
    .firstOrNull { it.isRight() }
    ?.map { it.toBitmapDrawable(context) }
    ?.getOrNull()
    ?: getThumbnailBitmapDrawableBlocking(context)

suspend inline fun getVideoCoverWithPaletteAsync(
    context: Context,
    videoMetadata: VideoMetadata,
    size: ImageSize? = null,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        videoMetadata
            .covers
            .map { getBitmapFromUrlWithPaletteCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.map { (palette, bitmap) ->
                palette to bitmap.toBitmapDrawable(context)
            }
            ?.getOrNull()
            ?: getThumbnailBitmapDrawableWithPalette(context)
    }
}

inline fun getVideoCoverWithPaletteBlocking(
    context: Context,
    videoMetadata: VideoMetadata,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = videoMetadata
    .covers
    .map { getBitmapFromUrlWithPaletteBlockingCatching(context, it, size, bitmapSettings) }
    .firstOrNull { it.isRight() }
    ?.map { (palette, bitmap) ->
        palette to bitmap.toBitmapDrawable(context)
    }
    ?.getOrNull()
    ?: getThumbnailBitmapDrawableWithPaletteBlocking(context)