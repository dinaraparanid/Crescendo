package com.paranid5.crescendo.ui.covers

import android.content.Context
import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.media.images.getTrackCoverAsync
import com.paranid5.crescendo.core.media.images.getTrackCoverWithPaletteAsync

suspend fun trackCoverModel(
    context: Context,
    trackPath: String?,
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {},
) = ImageRequest.Builder(context)
    .data(
        getTrackCoverAsync(
            context = context,
            path = trackPath,
            size = size,
            bitmapSettings = bitmapSettings,
        ).await()
    )
    .defaultError()
    .defaultFallback()
    .applyTransformations(
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
    )
    .networkCachePolicy(CachePolicy.ENABLED)
    .diskCachePolicy(CachePolicy.ENABLED)
    .memoryCachePolicy(CachePolicy.ENABLED)
    .precision(Precision.EXACT)
    .scale(Scale.FILL)
    .crossfade(animationMillis)
    .build()

suspend fun trackCoverModelWithPalette(
    context: Context,
    trackPath: String?,
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<ImageRequest, Palette?> {
    val (palette, coverModel) = getTrackCoverWithPaletteAsync(
        context = context,
        path = trackPath,
        size = size,
        bitmapSettings = bitmapSettings,
    ).await()

    return ImageRequest.Builder(context)
        .data(coverModel)
        .defaultError()
        .defaultFallback()
        .applyTransformations(
            isPlaceholderRequired = isPlaceholderRequired,
            size = size,
            isBlured = isBlured,
        )
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .crossfade(animationMillis)
        .build() to palette
}
