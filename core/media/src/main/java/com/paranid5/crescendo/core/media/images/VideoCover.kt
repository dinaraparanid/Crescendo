package com.paranid5.crescendo.core.media.images

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

suspend fun getVideoCoverBitmapOrThumbnailAsync(
    context: Context,
    videoCovers: List<String>,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        videoCovers
            .asFlow()
            .map { getBitmapFromUrlCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.getOrNull()
    }
}

suspend fun downloadCoverBitmapAsync(
    context: Context,
    size: ImageSize? = null,
    vararg urls: String,
    bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        urls
            .asFlow()
            .map { getBitmapFromUrlCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.getOrNull()
    }
}

suspend fun getVideoCoverAsync(
    context: Context,
    videoCovers: List<String>,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        videoCovers
            .asFlow()
            .map { getBitmapFromUrlCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.map { it.toBitmapDrawable(context) }
            ?.getOrNull()
    }
}

suspend fun getVideoCoverWithPaletteAsync(
    context: Context,
    videoCovers: List<String>,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = coroutineScope {
    async(Dispatchers.IO) {
        videoCovers
            .asFlow()
            .map { getBitmapFromUrlWithPaletteCatching(context, it, size, bitmapSettings) }
            .firstOrNull { it.isRight() }
            ?.map { (palette, bitmap) -> palette to bitmap.toBitmapDrawable(context) }
            ?.getOrNull()
            .let { it?.first to it?.second }
    }
}
