package com.paranid5.mediastreamer.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toDrawable
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.VideoMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GlideUtils(private val context: Context) {
    private inline val bitmapGlideBuilder
        get() = Glide.with(context).asBitmap()

    private inline val Bitmap.withPalette
        get() = Palette.from(this).generate() to this

    internal inline val thumbnailBitmap: Bitmap
        get() = bitmapGlideBuilder
            .load(R.drawable.cover_thumbnail)
            .submit()
            .get()

    internal inline val thumbnailBitmapWithPalette
        get() = thumbnailBitmap.withPalette

    private inline fun getBitmapFromModel(
        model: Any,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit = {}
    ): Bitmap = bitmapGlideBuilder
        .load(model)
        .run { size?.run { override(first, second) } ?: this }
        .submit()
        .get()
        .let { size?.run { Bitmap.createScaledBitmap(it, first, second, true) } ?: it }
        .also(bitmapSettings)

    private inline fun getBitmapFromResource(
        @DrawableRes res: Int,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromModel(res, size, bitmapSettings)

    internal inline fun getBitmapFromResourceCatching(
        @DrawableRes res: Int,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = runCatching { getBitmapFromResource(res, size, bitmapSettings) }

    private inline fun getBitmapFromUrl(
        url: String,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromModel(url, size, bitmapSettings)

    private inline fun getBitmapFromUrlWithPalette(
        url: String,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromUrl(url, size, bitmapSettings).withPalette

    internal inline fun getBitmapFromUrlCatching(
        url: String,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = kotlin.runCatching { getBitmapFromUrl(url, size, bitmapSettings) }

    internal inline fun getBitmapFromUrlWithPaletteCatching(
        url: String,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = kotlin.runCatching {
        getBitmapFromUrlWithPalette(url, size, bitmapSettings)
    }

    internal suspend inline fun getVideoCoverBitmapAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            videoMetadata
                .covers
                .asSequence()
                .map { getBitmapFromUrlCatching(it, size, bitmapSettings) }
                .firstOrNull { it.isSuccess }
                ?.getOrNull()
                ?: thumbnailBitmap
        }
    }

    internal suspend inline fun getVideoCoverBitmapWithPaletteAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            videoMetadata
                .covers
                .asSequence()
                .map { getBitmapFromUrlWithPaletteCatching(it, size, bitmapSettings) }
                .firstOrNull { it.isSuccess }
                ?.getOrNull()
                ?: thumbnailBitmapWithPalette
        }
    }

    internal suspend inline fun getVideoCoverAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            (videoMetadata
                .covers
                .asSequence()
                .map { getBitmapFromUrlCatching(it, size, bitmapSettings) }
                .firstOrNull { it.isSuccess }
                ?.getOrNull()
                ?: thumbnailBitmap)
                .toDrawable(context.resources)
        }
    }

    internal suspend inline fun getVideoCoverWithPaletteAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            (videoMetadata
                .covers
                .asSequence()
                .map { getBitmapFromUrlWithPaletteCatching(it, size, bitmapSettings) }
                .firstOrNull { it.isSuccess }
                ?.getOrNull()
                ?: thumbnailBitmapWithPalette)
                .let { (palette, bitmap) ->
                    palette to bitmap.toDrawable(context.resources)
                }
        }
    }
}