package com.paranid5.mediastreamer.presentation.ui

import android.content.Context
import android.graphics.Bitmap
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

    private fun getBitmapFromUrl(url: String, size: Pair<Int, Int>?): Bitmap =
        bitmapGlideBuilder
            .load(url)
            .run { size?.run { override(first, second) } ?: this }
            .submit()
            .get()
            .let { size?.run { Bitmap.createScaledBitmap(it, first, second, true) } ?: it }

    private fun getBitmapFromUrlWithPalette(url: String, size: Pair<Int, Int>?) =
        getBitmapFromUrl(url, size).withPalette

    fun getBitmapFromUrlCatching(url: String, size: Pair<Int, Int>? = null) =
        kotlin.runCatching { getBitmapFromUrl(url, size) }

    fun getBitmapFromUrlWithPaletteCatching(url: String, size: Pair<Int, Int>? = null) =
        kotlin.runCatching { getBitmapFromUrlWithPalette(url, size) }

    internal suspend inline fun getVideoCoverBitmapAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null
    ) = coroutineScope {
        async(Dispatchers.IO) {
            videoMetadata
                .covers
                .asSequence()
                .map { getBitmapFromUrlCatching(it, size) }
                .firstOrNull { it.isSuccess }
                ?.getOrNull()
                ?: thumbnailBitmap
        }
    }

    internal suspend inline fun getVideoCoverBitmapWithPaletteAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null
    ) = coroutineScope {
        async(Dispatchers.IO) {
            videoMetadata
                .covers
                .asSequence()
                .map { getBitmapFromUrlWithPaletteCatching(it, size) }
                .firstOrNull { it.isSuccess }
                ?.getOrNull()
                ?: thumbnailBitmapWithPalette
        }
    }

    internal suspend inline fun getVideoCoverAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null
    ) = coroutineScope {
        async(Dispatchers.IO) {
            (videoMetadata
                .covers
                .asSequence()
                .map { getBitmapFromUrlCatching(it, size) }
                .firstOrNull { it.isSuccess }
                ?.getOrNull()
                ?: thumbnailBitmap)
                .toDrawable(context.resources)
        }
    }

    internal suspend inline fun getVideoCoverWithPaletteAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null
    ) = coroutineScope {
        async(Dispatchers.IO) {
            (videoMetadata
                .covers
                .asSequence()
                .map { getBitmapFromUrlWithPaletteCatching(it, size) }
                .firstOrNull { it.isSuccess }
                ?.getOrNull()
                ?: thumbnailBitmapWithPalette)
                .let { (palette, bitmap) ->
                    palette to bitmap.toDrawable(context.resources)
                }
        }
    }
}