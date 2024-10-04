package com.paranid5.crescendo.ui.covers

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.BlurTransformation

private const val DefaultAnimationDuration = 400

suspend fun mediaCoverModelWithPalette(
    context: Context,
    videoCovers: List<String>,
    trackPath: String?,
    playbackStatus: PlaybackStatus,
    size: ImageSize,
) = when (playbackStatus) {
    PlaybackStatus.STREAMING -> videoCoverModelWithPalette(
        context = context,
        videoCovers = videoCovers,
        size = size,
    )

    PlaybackStatus.PLAYING -> trackCoverModelWithPalette(
        context = context,
        trackPath = trackPath,
        size = size,
    )
}

fun coverModel(
    data: Any?,
    context: Context,
    animationMillis: Int = DefaultAnimationDuration,
): ImageRequest = ImageRequest.Builder(context)
    .data(data)
    .networkCachePolicy(CachePolicy.ENABLED)
    .diskCachePolicy(CachePolicy.ENABLED)
    .memoryCachePolicy(CachePolicy.ENABLED)
    .precision(Precision.EXACT)
    .scale(Scale.FILL)
    .crossfade(animationMillis)
    .build()

internal fun ImageRequest.Builder.prevCoverPlaceholder(prevCoverModel: BitmapDrawable?) =
    when (prevCoverModel) {
        null -> placeholder(R.drawable.cover_thumbnail)
        else -> placeholder(prevCoverModel)
    }

internal fun ImageRequest.Builder.prevCoverError(prevCoverModel: BitmapDrawable?) =
    when (prevCoverModel) {
        null -> error(R.drawable.cover_thumbnail)
        else -> error(prevCoverModel)
    }

internal fun ImageRequest.Builder.prevCoverFallback(prevCoverModel: BitmapDrawable?) =
    when (prevCoverModel) {
        null -> fallback(R.drawable.cover_thumbnail)
        else -> fallback(prevCoverModel)
    }

internal fun ImageRequest.Builder.defaultPlaceholder() =
    placeholder(R.drawable.cover_thumbnail)

internal fun ImageRequest.Builder.defaultError() =
    error(R.drawable.cover_thumbnail)

internal fun ImageRequest.Builder.defaultFallback() =
    fallback(R.drawable.cover_thumbnail)

internal fun ImageRequest.Builder.applyTransformations(
    isPlaceholderRequired: Boolean,
    size: ImageSize?,
    isBlured: Boolean,
) = apply {
    if (isPlaceholderRequired)
        defaultPlaceholder()

    if (isBlured)
        transformations(BlurTransformation())

    size?.run { size(width, height) }
}
