package com.paranid5.crescendo.ui.covers

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.media.images.getTrackCoverAsync
import com.paranid5.crescendo.core.media.images.getTrackCoverWithPaletteAsync
import com.paranid5.crescendo.ui.track.currentTrackState

@Composable
fun trackCoverModel(
    path: String?,
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = false,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {}
): ImageRequest {
    val context = LocalContext.current
    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var prevCoverModel by remember { mutableStateOf<BitmapDrawable?>(null) }

    LaunchedEffect(key1 = path, key2 = size) {
        prevCoverModel = coverModel
        coverModel = getTrackCoverAsync(context, path, size, bitmapSettings).await()
    }

    return ImageRequest.Builder(context)
        .data(coverModel)
        .prevCoverErrorOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .prevCoverFallbackOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .applyTransformations(
            isPlaceholderRequired = isPlaceholderRequired,
            size = size,
            isBlured = isBlured,
            usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
            prevCoverModel = prevCoverModel
        )
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .crossfade(animationMillis)
        .build()
}

@Composable
fun rememberTrackCoverPainter(
    path: String?,
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = false,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {}
) = rememberAsyncImagePainter(
    model = trackCoverModel(
        path = path,
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        bitmapSettings = bitmapSettings
    )
)

@Composable
fun trackCoverModelWithPalette(
    path: String?,
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<ImageRequest, Palette?> {
    val context = LocalContext.current
    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var prevCoverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var palette by remember { mutableStateOf<Palette?>(null) }

    LaunchedEffect(key1 = path, key2 = size) {
        val (plt, cover) =
            getTrackCoverWithPaletteAsync(context, path, size, bitmapSettings).await()

        prevCoverModel = coverModel
        coverModel = cover
        palette = plt
    }

    return ImageRequest.Builder(context)
        .data(coverModel)
        .prevCoverErrorOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .prevCoverFallbackOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .applyTransformations(
            isPlaceholderRequired = isPlaceholderRequired,
            size = size,
            isBlured = isBlured,
            usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
            prevCoverModel = prevCoverModel
        )
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .crossfade(animationMillis)
        .build() to palette
}

@Composable
fun rememberTrackCoverPainterWithPalette(
    path: String?,
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<AsyncImagePainter, Palette?> {
    val (coverModel, palette) = trackCoverModelWithPalette(
        path = path,
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        bitmapSettings = bitmapSettings
    )

    return rememberAsyncImagePainter(coverModel) to palette
}

@Composable
fun currentTrackCoverModel(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {},
): ImageRequest {
    val curTrack by currentTrackState()

    return trackCoverModel(
        path = curTrack?.path,
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        bitmapSettings = bitmapSettings
    )
}

@Composable
fun rememberCurrentTrackCoverPainter(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {}
) = rememberAsyncImagePainter(
    model = currentTrackCoverModel(
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        bitmapSettings = bitmapSettings,
    )
)

@Composable
fun currentTrackCoverModelWithPalette(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<ImageRequest, Palette?> {
    val curTrack by currentTrackState()

    return trackCoverModelWithPalette(
        path = curTrack?.path,
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        bitmapSettings = bitmapSettings
    )
}

@Composable
fun rememberCurrentTrackCoverPainterWithPalette(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<AsyncImagePainter, Palette?> {
    val (coverModel, palette) = currentTrackCoverModelWithPalette(
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        bitmapSettings = bitmapSettings
    )

    return rememberAsyncImagePainter(coverModel) to palette
}