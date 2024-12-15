package com.paranid5.crescendo.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import arrow.core.Either
import com.paranid5.crescendo.data.image.CommonImageRetriever.getBitmapFromModel
import com.paranid5.crescendo.data.image.CommonImageRetriever.getBitmapFromModelBlocking
import com.paranid5.crescendo.domain.image.model.BitmapDrawableWithPalette
import com.paranid5.crescendo.domain.image.model.BitmapWithPalette
import com.paranid5.crescendo.domain.image.model.ImageResource
import com.paranid5.crescendo.domain.image.model.ImageSize
import com.paranid5.crescendo.domain.image.utils.toBitmapDrawable
import com.paranid5.crescendo.domain.image.utils.withPalette

internal object ResourceRetriever {
    suspend fun getBitmapFromResource(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Bitmap = getBitmapFromModel(
        context = context,
        model = res.value,
        size = size,
        bitmapMutation = bitmapMutation,
    )

    suspend fun getBitmapDrawableFromResource(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawable = getBitmapFromResource(
        context = context,
        res = res,
        size = size,
        bitmapMutation = bitmapMutation,
    ).toBitmapDrawable(context)

    fun getBitmapFromResourceBlocking(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Bitmap = getBitmapFromModelBlocking(
        context = context,
        model = res.value,
        size = size,
        bitmapMutation = bitmapMutation,
    )

    fun getBitmapDrawableFromResourceBlocking(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawable = getBitmapFromResourceBlocking(
        context = context,
        res = res,
        size = size,
        bitmapMutation = bitmapMutation,
    ).toBitmapDrawable(context)

    suspend fun getBitmapFromResourceCatching(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, Bitmap> = Either.catch {
        getBitmapFromResource(
            context = context,
            res = res,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    suspend fun getBitmapDrawableFromResourceCatching(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawable> = Either.catch {
        getBitmapDrawableFromResource(
            context = context,
            res = res,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapFromResourceBlockingCatching(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, Bitmap> = Either.catch {
        getBitmapFromResourceBlocking(
            context = context,
            res = res,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapDrawableFromResourceBlockingCatching(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawable> = Either.catch {
        getBitmapDrawableFromResourceBlocking(
            context = context,
            res = res,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    suspend fun getBitmapFromResourceWithPalette(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapWithPalette = getBitmapFromModel(
        context = context,
        model = res.value,
        size = size,
        bitmapMutation = bitmapMutation,
    ).withPalette

    suspend fun getBitmapDrawableFromResourceWithPalette(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawableWithPalette = getBitmapFromResourceWithPalette(
        context = context,
        res = res,
        size = size,
        bitmapMutation = bitmapMutation,
    ).let { BitmapDrawableWithPalette.fromBitmapWithPalette(context, it) }

    fun getBitmapFromResourceWithPaletteBlocking(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapWithPalette = getBitmapFromModelBlocking(
        context = context,
        model = res.value,
        size = size,
        bitmapMutation = bitmapMutation,
    ).withPalette

    fun getBitmapDrawableFromResourceWithPaletteBlocking(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawableWithPalette = getBitmapFromResourceWithPaletteBlocking(
        context = context,
        res = res,
        size = size,
        bitmapMutation = bitmapMutation,
    ).let { BitmapDrawableWithPalette.fromBitmapWithPalette(context, it) }

    suspend fun getBitmapFromResourceWithPaletteCatching(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapWithPalette> = Either.catch {
        getBitmapFromResource(
            context = context,
            res = res,
            size = size,
            bitmapMutation = bitmapMutation,
        ).withPalette
    }

    suspend fun getBitmapDrawableFromResourceWithPaletteCatching(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawableWithPalette> = Either.catch {
        getBitmapDrawableFromResourceWithPalette(
            context = context,
            res = res,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapFromResourceWithPaletteBlockingCatching(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapWithPalette> = Either.catch {
        getBitmapFromResourceWithPaletteBlocking(
            context = context,
            res = res,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapDrawableFromResourceWithPaletteBlockingCatching(
        context: Context,
        res: ImageResource,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawableWithPalette> = Either.catch {
        getBitmapDrawableFromResourceWithPaletteBlocking(
            context = context,
            res = res,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }
}
