package com.paranid5.crescendo.ui.covers

import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.palette.graphics.Palette
import coil.request.ImageRequest
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.BlurTransformation

@Composable
fun coverModelWithPalette(
    audioStatus: AudioStatus,
    size: ImageSize,
): Pair<ImageRequest, Palette?> = when (audioStatus) {
    AudioStatus.STREAMING -> videoCoverModelWithPalette(
        isPlaceholderRequired = false,
        size = size,
    )

    AudioStatus.PLAYING -> currentTrackCoverModelWithPalette(
        isPlaceholderRequired = false,
        size = size,
    )
}

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
