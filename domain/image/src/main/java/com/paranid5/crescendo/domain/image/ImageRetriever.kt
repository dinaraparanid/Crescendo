package com.paranid5.crescendo.domain.image

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.paranid5.crescendo.domain.image.model.BitmapWithPalette
import com.paranid5.crescendo.domain.image.model.ImagePath
import com.paranid5.crescendo.domain.image.model.ImageResource
import com.paranid5.crescendo.domain.image.model.ImageUrl
import com.paranid5.crescendo.domain.image.model.BitmapDrawableWithPalette

interface ImageRetriever {
    suspend fun downloadBitmap(url: ImageUrl): Bitmap?
    suspend fun downloadBitmapWithPalette(url: ImageUrl): BitmapWithPalette?
    suspend fun downloadBitmapDrawable(url: ImageUrl): BitmapDrawable?
    suspend fun downloadBitmapDrawableWithPalette(url: ImageUrl): BitmapDrawableWithPalette?
    suspend fun downloadImageData(url: ImageUrl): ByteArray?

    suspend fun retrieveBitmapFromMedia(path: ImagePath): Bitmap?
    suspend fun retrieveBitmapFromMediaWithPalette(path: ImagePath): BitmapWithPalette?
    suspend fun retrieveBitmapDrawableFromMedia(path: ImagePath): BitmapDrawable?
    suspend fun retrieveBitmapDrawableFromMediaWithPalette(path: ImagePath): BitmapDrawableWithPalette?
    suspend fun retrieveImageData(path: ImagePath): ByteArray?

    suspend fun resourceBitmap(resource: ImageResource): Bitmap?
    suspend fun resourceBitmapWithPalette(resource: ImageResource): BitmapWithPalette?
    suspend fun resourceBitmapDrawable(resource: ImageResource): BitmapDrawable?
    suspend fun resourceBitmapDrawableWithPalette(resource: ImageResource): BitmapDrawableWithPalette?
    suspend fun resourceImageData(resource: ImageResource): ByteArray?
}
