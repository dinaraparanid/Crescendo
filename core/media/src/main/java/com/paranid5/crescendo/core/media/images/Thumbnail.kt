package com.paranid5.crescendo.core.media.images

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import coil.executeBlocking
import com.paranid5.crescendo.core.resources.R

fun @receiver:DrawableRes Int.getCoilModel(context: Context) =
    getCoilModelBuilder(context).build()

suspend fun getThumbnailBitmap(context: Context) =
    ImageLoaderBuilder(context)
        .build()
        .execute(R.drawable.cover_thumbnail.getCoilModel(context))
        .drawable!!
        .toBitmap()

fun getThumbnailBitmapBlocking(context: Context) =
    ImageLoaderBuilder(context)
        .build()
        .executeBlocking(R.drawable.cover_thumbnail.getCoilModel(context))
        .drawable!!
        .toBitmap()

suspend fun getThumbnailBitmapDrawable(context: Context) =
    getThumbnailBitmap(context).toBitmapDrawable(context)

fun getThumbnailBitmapDrawableBlocking(context: Context) =
    getThumbnailBitmapBlocking(context).toBitmapDrawable(context)

suspend fun getThumbnailBitmapWithPalette(context: Context) =
    getThumbnailBitmap(context).withPalette

fun getThumbnailBitmapWithPaletteBlocking(context: Context) =
    getThumbnailBitmapBlocking(context).withPalette

suspend fun getThumbnailBitmapDrawableWithPalette(context: Context) =
    getThumbnailBitmapWithPalette(context).let { (palette, bitmap) ->
        palette to bitmap.toBitmapDrawable(context)
    }

fun getThumbnailBitmapDrawableWithPaletteBlocking(context: Context) =
    getThumbnailBitmapWithPaletteBlocking(context).let { (palette, bitmap) ->
        palette to bitmap.toBitmapDrawable(context)
    }