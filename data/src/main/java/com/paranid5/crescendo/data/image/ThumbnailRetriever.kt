package com.paranid5.crescendo.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.paranid5.crescendo.core.resources.ui.CommonDrawable
import com.paranid5.crescendo.data.image.ResourceRetriever.getBitmapDrawableFromResource
import com.paranid5.crescendo.data.image.ResourceRetriever.getBitmapDrawableFromResourceBlocking
import com.paranid5.crescendo.data.image.ResourceRetriever.getBitmapFromResource
import com.paranid5.crescendo.data.image.ResourceRetriever.getBitmapFromResourceBlocking
import com.paranid5.crescendo.domain.image.model.BitmapDrawableWithPalette
import com.paranid5.crescendo.domain.image.model.BitmapWithPalette
import com.paranid5.crescendo.domain.image.model.ImageResource
import com.paranid5.crescendo.domain.image.utils.withPalette

internal object ThumbnailRetriever {
    private val resource = ImageResource(CommonDrawable.cover_thumbnail)

    suspend fun getCoverThumbnailBitmap(context: Context): Bitmap =
        getBitmapFromResource(context = context, res = resource)

    fun getCoverThumbnailBitmapBlocking(context: Context): Bitmap =
        getBitmapFromResourceBlocking(context = context, res = resource)

    suspend fun getCoverThumbnailBitmapDrawable(context: Context): BitmapDrawable =
        getBitmapDrawableFromResource(context = context, res = resource)

    fun getCoverThumbnailBitmapDrawableBlocking(context: Context): BitmapDrawable =
        getBitmapDrawableFromResourceBlocking(context = context, res = resource)

    suspend fun getCoverThumbnailBitmapWithPalette(context: Context): BitmapWithPalette =
        getCoverThumbnailBitmap(context).withPalette

    fun getCoverThumbnailBitmapWithPaletteBlocking(context: Context): BitmapWithPalette =
        getCoverThumbnailBitmapBlocking(context).withPalette

    suspend fun getCoverThumbnailBitmapDrawableWithPalette(context: Context): BitmapDrawableWithPalette =
        getCoverThumbnailBitmapWithPalette(context).let {
            BitmapDrawableWithPalette.fromBitmapWithPalette(context, it)
        }

    fun getCoverThumbnailBitmapDrawableWithPaletteBlocking(context: Context): BitmapDrawableWithPalette =
        getCoverThumbnailBitmapWithPaletteBlocking(context).let {
            BitmapDrawableWithPalette.fromBitmapWithPalette(context, it)
        }
}
