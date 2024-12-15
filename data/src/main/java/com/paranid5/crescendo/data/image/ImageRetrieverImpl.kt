package com.paranid5.crescendo.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.paranid5.crescendo.domain.image.ImageRetriever
import com.paranid5.crescendo.domain.image.model.BitmapDrawableWithPalette
import com.paranid5.crescendo.domain.image.model.BitmapWithPalette
import com.paranid5.crescendo.domain.image.model.ImagePath
import com.paranid5.crescendo.domain.image.model.ImageResource
import com.paranid5.crescendo.domain.image.model.ImageUrl

internal class ImageRetrieverImpl(private val context: Context) : ImageRetriever {
    override suspend fun downloadBitmap(url: ImageUrl): Bitmap? =
        UrlRetriever.getBitmapFromUrlCatching(context, url).getOrNull()

    override suspend fun downloadBitmapWithPalette(url: ImageUrl): BitmapWithPalette? =
        UrlRetriever.getBitmapFromUrlWithPaletteCatching(context, url).getOrNull()

    override suspend fun downloadBitmapDrawable(url: ImageUrl): BitmapDrawable? =
        UrlRetriever.getBitmapDrawableFromUrlCatching(context, url).getOrNull()

    override suspend fun downloadBitmapDrawableWithPalette(url: ImageUrl): BitmapDrawableWithPalette? =
        UrlRetriever.getBitmapDrawableFromUrlWithPaletteCatching(context, url).getOrNull()

    override suspend fun downloadImageData(url: ImageUrl): ByteArray? =
        BinaryDataRetriever.getImageBinaryDataFromUrlCatching(context, url).getOrNull()

    override suspend fun retrieveBitmapFromMedia(path: ImagePath): Bitmap? =
        PathRetriever.getBitmapFromPathCatching(context, path).getOrNull()

    override suspend fun retrieveBitmapFromMediaWithPalette(path: ImagePath): BitmapWithPalette? =
        PathRetriever.getBitmapFromPathWithPaletteCatching(context, path).getOrNull()

    override suspend fun retrieveBitmapDrawableFromMedia(path: ImagePath): BitmapDrawable? =
        PathRetriever.getBitmapDrawableFromPathCatching(context, path).getOrNull()

    override suspend fun retrieveBitmapDrawableFromMediaWithPalette(path: ImagePath): BitmapDrawableWithPalette? =
        PathRetriever.getBitmapDrawableFromPathWithPaletteCatching(context, path).getOrNull()

    override suspend fun retrieveImageData(path: ImagePath): ByteArray? =
        BinaryDataRetriever.getImageBinaryDataFromPathCatching(context, path).getOrNull()

    override suspend fun resourceBitmap(resource: ImageResource): Bitmap? =
        com.paranid5.crescendo.data.image.ResourceRetriever.getBitmapFromResourceCatching(context, resource).getOrNull()

    override suspend fun resourceBitmapWithPalette(resource: ImageResource): BitmapWithPalette? =
        com.paranid5.crescendo.data.image.ResourceRetriever.getBitmapFromResourceWithPaletteCatching(context, resource).getOrNull()

    override suspend fun resourceBitmapDrawable(resource: ImageResource): BitmapDrawable? =
        com.paranid5.crescendo.data.image.ResourceRetriever.getBitmapDrawableFromResourceCatching(context, resource).getOrNull()

    override suspend fun resourceBitmapDrawableWithPalette(resource: ImageResource): BitmapDrawableWithPalette? =
        com.paranid5.crescendo.data.image.ResourceRetriever.getBitmapDrawableFromResourceWithPaletteCatching(context, resource).getOrNull()

    override suspend fun resourceImageData(resource: ImageResource): ByteArray? =
        BinaryDataRetriever.getImageBinaryDataFromResourceCatching(context, resource).getOrNull()
}
