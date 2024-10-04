package com.paranid5.crescendo.ui.covers

import android.content.Context
import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.media.images.getVideoCoverAsync
import com.paranid5.crescendo.core.media.images.getVideoCoverWithPaletteAsync

suspend fun videoCoverModel(
    context: Context,
    videoCovers: List<String>,
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getVideoCoverAsync(
    context = context,
    videoCovers = videoCovers,
    size = size,
    bitmapSettings = bitmapSettings,
).await()?.let { model ->
    ImageRequest.Builder(context)
        .data(model)
        .applyTransformations(
            isPlaceholderRequired = isPlaceholderRequired,
            size = size,
            isBlured = isBlured,
        )
        .defaultError()
        .defaultFallback()
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .crossfade(animationMillis)
        .build()
}

suspend fun videoCoverModelWithPalette(
    context: Context,
    videoCovers: List<String>,
    isPlaceholderRequired: Boolean = false,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<ImageRequest?, Palette?> {
    val (palette, coverModel) = getVideoCoverWithPaletteAsync(
        context = context,
        videoCovers = videoCovers,
        size = size,
        bitmapSettings = bitmapSettings,
    ).await()

    return coverModel?.let {
        ImageRequest.Builder(context)
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
            .build()
    } to palette
}
