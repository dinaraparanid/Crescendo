package com.paranid5.crescendo.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import arrow.core.Either
import com.paranid5.crescendo.data.image.ThumbnailRetriever.getCoverThumbnailBitmap
import com.paranid5.crescendo.data.image.ThumbnailRetriever.getCoverThumbnailBitmapBlocking
import com.paranid5.crescendo.domain.image.model.BitmapDrawableWithPalette
import com.paranid5.crescendo.domain.image.model.BitmapWithPalette
import com.paranid5.crescendo.domain.image.model.ImagePath
import com.paranid5.crescendo.domain.image.model.ImageSize
import com.paranid5.crescendo.domain.image.utils.toBitmap
import com.paranid5.crescendo.domain.image.utils.toBitmapDrawable
import com.paranid5.crescendo.domain.image.utils.withPalette

internal object PathRetriever {
    fun getImageDataByPath(path: ImagePath): ByteArray? =
        MediaMetadataRetriever().apply { setDataSource(path.value) }.embeddedPicture

    suspend fun getBitmapFromPath(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Bitmap = getImageDataByPath(path)
        ?.toBitmap(imageSize = size)
        ?.also { bitmapMutation?.invoke(it) }
        ?: getCoverThumbnailBitmap(context)

    suspend fun getBitmapDrawableFromPath(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawable = getBitmapFromPath(
        context = context,
        path = path,
        size = size,
        bitmapMutation = bitmapMutation,
    ).toBitmapDrawable(context)

    fun getBitmapFromPathBlocking(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Bitmap = getImageDataByPath(path)
        ?.toBitmap(imageSize = size)
        ?.also { bitmapMutation?.invoke(it) }
        ?: getCoverThumbnailBitmapBlocking(context)

    fun getBitmapDrawableFromPathBlocking(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawable = getBitmapFromPathBlocking(
        context = context,
        path = path,
        size = size,
        bitmapMutation = bitmapMutation,
    ).toBitmapDrawable(context)

    suspend fun getBitmapFromPathWithPalette(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapWithPalette = getBitmapFromPath(
        context = context,
        path = path,
        size = size,
        bitmapMutation = bitmapMutation,
    ).withPalette

    suspend fun getBitmapDrawableFromPathWithPalette(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawableWithPalette = getBitmapFromPathWithPalette(
        context = context,
        path = path,
        size = size,
        bitmapMutation = bitmapMutation,
    ).let { BitmapDrawableWithPalette.fromBitmapWithPalette(context, it) }

    fun getBitmapFromPathWithPaletteBlocking(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapWithPalette = getBitmapFromPathBlocking(
        context = context,
        path = path,
        size = size,
        bitmapMutation = bitmapMutation,
    ).withPalette

    fun getBitmapDrawableFromPathWithPaletteBlocking(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawableWithPalette = getBitmapFromPathWithPaletteBlocking(
        context = context,
        path = path,
        size = size,
        bitmapMutation = bitmapMutation,
    ).let { BitmapDrawableWithPalette.fromBitmapWithPalette(context, it) }

    suspend fun getBitmapFromPathCatching(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, Bitmap> = Either.catch {
        getBitmapFromPath(
            context = context,
            path = path,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    suspend fun getBitmapDrawableFromPathCatching(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawable> = Either.catch {
        getBitmapDrawableFromPath(
            context = context,
            path = path,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapFromPathBlockingCatching(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, Bitmap> = Either.catch {
        getBitmapFromPathBlocking(
            context = context,
            path = path,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapDrawableFromPathBlockingCatching(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawable> = Either.catch {
        getBitmapDrawableFromPathBlocking(
            context = context,
            path = path,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    suspend fun getBitmapFromPathWithPaletteCatching(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapWithPalette> = Either.catch {
        getBitmapFromPathWithPalette(
            context = context,
            path = path,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    suspend fun getBitmapDrawableFromPathWithPaletteCatching(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawableWithPalette> = Either.catch {
        getBitmapDrawableFromPathWithPalette(
            context = context,
            path = path,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapFromPathWithPaletteBlockingCatching(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapWithPalette> = Either.catch {
        getBitmapFromPathWithPaletteBlocking(
            context = context,
            path = path,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapDrawableFromPathWithPaletteBlockingCatching(
        context: Context,
        path: ImagePath,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawableWithPalette> = Either.catch {
        getBitmapDrawableFromPathWithPaletteBlocking(
            context = context,
            path = path,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }
}
