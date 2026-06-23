package com.paranid5.crescendo.ui.covers

import android.content.Context
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.BlurTransformation

private const val DefaultAnimationDuration = 400

@Deprecated("Will be removed")
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

@Deprecated("Will be removed")
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
