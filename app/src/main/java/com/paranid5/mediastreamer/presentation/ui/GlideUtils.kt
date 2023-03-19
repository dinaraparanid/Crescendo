package com.paranid5.mediastreamer.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.VideoMetadata
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GlideUtils(private val context: Context) {
    private inline val bitmapGlideBuilder
        get() = Glide.with(context).asBitmap()

    internal inline val thumbnailBitmap: Bitmap
        get() = bitmapGlideBuilder
            .load(R.drawable.cover_thumbnail)
            .submit()
            .get()

    private fun getBitmapFromUrl(url: String): Bitmap =
        bitmapGlideBuilder
            .load(url)
            .submit()
            .get()

    fun getBitmapFromUrlCatching(url: String) =
        kotlin.runCatching { getBitmapFromUrl(url) }

    internal suspend inline fun getVideoCoverAsync(videoMetadata: VideoMetadata): Deferred<Bitmap> =
        coroutineScope {
            async(Dispatchers.IO) {
                videoMetadata
                    .covers
                    .asSequence()
                    .map(::getBitmapFromUrlCatching)
                    .firstOrNull { it.isSuccess }
                    ?.getOrNull()
                    ?: thumbnailBitmap
            }
        }
}