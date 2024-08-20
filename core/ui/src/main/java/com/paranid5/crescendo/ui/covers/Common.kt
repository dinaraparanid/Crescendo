package com.paranid5.crescendo.ui.covers

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.palette.graphics.Palette
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.BlurTransformation

private const val DefaultAnimationDuration = 400

@Composable
fun mediaCoverModelWithPalette(
    playbackStatus: PlaybackStatus,
    size: ImageSize,
): Pair<ImageRequest, Palette?> = when (playbackStatus) {
    PlaybackStatus.STREAMING -> videoCoverModelWithPalette(
        isPlaceholderRequired = false,
        size = size,
    )

    PlaybackStatus.PLAYING -> currentTrackCoverModelWithPalette(
        isPlaceholderRequired = false,
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

internal fun ImageRequest.Builder.prevCoverPlaceholderOrDefault(
    usePrevCoverAsPlaceholder: Boolean,
    prevCoverModel: BitmapDrawable?,
) = when {
    usePrevCoverAsPlaceholder -> prevCoverPlaceholder(prevCoverModel)
    else -> placeholder(R.drawable.cover_thumbnail)
}

internal fun ImageRequest.Builder.prevCoverErrorOrDefault(
    usePrevCoverAsPlaceholder: Boolean,
    prevCoverModel: BitmapDrawable?,
) = when {
    usePrevCoverAsPlaceholder -> prevCoverError(prevCoverModel)
    else -> error(R.drawable.cover_thumbnail)
}

internal fun ImageRequest.Builder.prevCoverFallbackOrDefault(
    usePrevCoverAsPlaceholder: Boolean,
    prevCoverModel: BitmapDrawable?,
) = when {
    usePrevCoverAsPlaceholder -> prevCoverFallback(prevCoverModel)
    else -> fallback(R.drawable.cover_thumbnail)
}

internal fun ImageRequest.Builder.applyTransformations(
    isPlaceholderRequired: Boolean,
    size: ImageSize?,
    isBlured: Boolean,
    usePrevCoverAsPlaceholder: Boolean,
    prevCoverModel: BitmapDrawable?
) = apply {
    if (isPlaceholderRequired)
        prevCoverPlaceholderOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)

    if (isBlured)
        transformations(BlurTransformation())

    size?.run { size(width, height) }
}
