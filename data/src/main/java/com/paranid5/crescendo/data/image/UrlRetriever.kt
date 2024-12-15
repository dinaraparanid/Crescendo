package com.paranid5.crescendo.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import arrow.core.Either
import com.paranid5.crescendo.data.image.CommonImageRetriever.getBitmapFromModel
import com.paranid5.crescendo.data.image.CommonImageRetriever.getBitmapFromModelBlocking
import com.paranid5.crescendo.domain.image.model.BitmapDrawableWithPalette
import com.paranid5.crescendo.domain.image.model.BitmapWithPalette
import com.paranid5.crescendo.domain.image.model.ImageSize
import com.paranid5.crescendo.domain.image.model.ImageUrl
import com.paranid5.crescendo.domain.image.utils.toBitmapDrawable
import com.paranid5.crescendo.domain.image.utils.withPalette

internal object UrlRetriever {
    suspend fun getBitmapFromUrl(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Bitmap = getBitmapFromModel(
        context = context,
        model = url.value,
        size = size,
        bitmapMutation = bitmapMutation,
    )

    suspend fun getBitmapDrawableFromUrl(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawable = getBitmapFromUrl(
        context = context,
        url = url,
        size = size,
        bitmapMutation = bitmapMutation,
    ).toBitmapDrawable(context)

    fun getBitmapFromUrlBlocking(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Bitmap = getBitmapFromModelBlocking(
        context = context,
        model = url.value,
        size = size,
        bitmapMutation = bitmapMutation,
    )

    fun getBitmapDrawableFromUrlBlocking(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawable = getBitmapFromUrlBlocking(
        context = context,
        url = url,
        size = size,
        bitmapMutation = bitmapMutation,
    ).toBitmapDrawable(context)

    suspend fun getBitmapFromUrlWithPalette(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapWithPalette = getBitmapFromUrl(
        context = context,
        url = url,
        size = size,
        bitmapMutation = bitmapMutation,
    ).withPalette

    suspend fun getBitmapDrawableFromUrlWithPalette(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawableWithPalette = getBitmapFromUrlWithPalette(
        context = context,
        url = url,
        size = size,
        bitmapMutation = bitmapMutation,
    ).let { BitmapDrawableWithPalette.fromBitmapWithPalette(context, it) }

    fun getBitmapFromUrlWithPaletteBlocking(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapWithPalette = getBitmapFromUrlBlocking(
        context = context,
        url = url,
        size = size,
        bitmapMutation = bitmapMutation,
    ).withPalette

    fun getBitmapDrawableFromUrlWithPaletteBlocking(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): BitmapDrawableWithPalette = getBitmapFromUrlWithPaletteBlocking(
        context = context,
        url = url,
        size = size,
        bitmapMutation = bitmapMutation,
    ).let { BitmapDrawableWithPalette.fromBitmapWithPalette(context, it) }

    suspend fun getBitmapFromUrlCatching(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, Bitmap> = Either.catch {
        getBitmapFromUrl(
            context = context,
            url = url,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    suspend fun getBitmapDrawableFromUrlCatching(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawable> = Either.catch {
        getBitmapFromUrl(
            context = context,
            url = url,
            size = size,
            bitmapMutation = bitmapMutation,
        ).toBitmapDrawable(context)
    }

    fun getBitmapFromUrlBlockingCatching(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, Bitmap> = Either.catch {
        getBitmapFromUrlBlocking(
            context = context,
            url = url,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapDrawableFromUrlBlockingCatching(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawable> = Either.catch {
        getBitmapFromUrlBlocking(
            context = context,
            url = url,
            size = size,
            bitmapMutation = bitmapMutation,
        ).toBitmapDrawable(context)
    }

    suspend fun getBitmapFromUrlWithPaletteCatching(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapWithPalette> = Either.catch {
        getBitmapFromUrlWithPalette(
            context = context,
            url = url,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    suspend fun getBitmapDrawableFromUrlWithPaletteCatching(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ): Either<Throwable, BitmapDrawableWithPalette> = Either.catch {
        getBitmapDrawableFromUrlWithPalette(
            context = context,
            url = url,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapFromUrlWithPaletteBlockingCatching(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ) = Either.catch {
        getBitmapFromUrlWithPaletteBlocking(
            context = context,
            url = url,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }

    fun getBitmapDrawableFromUrlWithPaletteBlockingCatching(
        context: Context,
        url: ImageUrl,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ) = Either.catch {
        getBitmapDrawableFromUrlWithPaletteBlocking(
            context = context,
            url = url,
            size = size,
            bitmapMutation = bitmapMutation,
        )
    }
}