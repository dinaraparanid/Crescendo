package com.paranid5.crescendo.core.media.images

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import coil.executeBlocking
import com.paranid5.crescendo.core.resources.R

@Deprecated("Will be removed")
fun @receiver:DrawableRes Int.getCoilModel(context: Context) =
    getCoilModelBuilder(context).build()

@Deprecated("Will be removed")
suspend fun getThumbnailBitmap(context: Context) =
    ImageLoaderBuilder(context)
        .build()
        .execute(R.drawable.cover_thumbnail.getCoilModel(context))
        .drawable!!
        .toBitmap()

@Deprecated("Will be removed")
fun getThumbnailBitmapBlocking(context: Context) =
    ImageLoaderBuilder(context)
        .build()
        .executeBlocking(R.drawable.cover_thumbnail.getCoilModel(context))
        .drawable!!
        .toBitmap()

@Deprecated("Will be removed")
suspend fun getThumbnailBitmapDrawable(context: Context) =
    getThumbnailBitmap(context).toBitmapDrawable(context)

@Deprecated("Will be removed")
fun getThumbnailBitmapDrawableBlocking(context: Context) =
    getThumbnailBitmapBlocking(context).toBitmapDrawable(context)

@Deprecated("Will be removed")
suspend fun getThumbnailBitmapWithPalette(context: Context) =
    getThumbnailBitmap(context).withPalette

@Deprecated("Will be removed")
fun getThumbnailBitmapWithPaletteBlocking(context: Context) =
    getThumbnailBitmapBlocking(context).withPalette

@Deprecated("Will be removed")
suspend fun getThumbnailBitmapDrawableWithPalette(context: Context) =
    getThumbnailBitmapWithPalette(context).let { (palette, bitmap) ->
        palette to bitmap.toBitmapDrawable(context)
    }

@Deprecated("Will be removed")
fun getThumbnailBitmapDrawableWithPaletteBlocking(context: Context) =
    getThumbnailBitmapWithPaletteBlocking(context).let { (palette, bitmap) ->
        palette to bitmap.toBitmapDrawable(context)
    }