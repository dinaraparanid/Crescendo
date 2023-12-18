package com.paranid5.crescendo.presentation.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.media.images.ImageSize
import com.paranid5.crescendo.media.images.getTrackCoverAsync
import com.paranid5.crescendo.media.images.getTrackCoverWithPaletteAsync
import com.paranid5.crescendo.media.images.getVideoCoverAsync
import com.paranid5.crescendo.media.images.getVideoCoverWithPaletteAsync
import com.paranid5.crescendo.presentation.ui.utils.BlurTransformation
import org.koin.compose.koinInject

@Composable
fun getVideoCoverModel(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
): ImageRequest {
    val metadata by storageHandler.currentMetadataState.collectAsState()
    val context = LocalContext.current

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var prevCoverModel by remember { mutableStateOf<BitmapDrawable?>(null) }

    LaunchedEffect(key1 = metadata) {
        val newModel = metadata?.let {
            getVideoCoverAsync(context, it, size, bitmapSettings)
                .await()
        }

        prevCoverModel = coverModel ?: newModel
        coverModel = newModel
    }

    return ImageRequest.Builder(context)
        .data(coverModel)
        .prevCoverErrorOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .prevCoverFallbackOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .applyTransformations(
            context = context,
            isPlaceholderRequired = isPlaceholderRequired,
            size = size,
            isBlured = isBlured,
            usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
            prevCoverModel = prevCoverModel
        )
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .crossfade(animationMillis)
        .build()
}

@Composable
fun rememberVideoCoverPainter(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
) = rememberAsyncImagePainter(
    model = getVideoCoverModel(
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        storageHandler = storageHandler,
        bitmapSettings = bitmapSettings
    )
)

@Composable
fun getVideoCoverModelWithPalette(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<ImageRequest, Palette?> {
    val metadata by storageHandler.currentMetadataState.collectAsState()
    val context = LocalContext.current

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var prevCoverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var palette by remember { mutableStateOf<Palette?>(null) }

    LaunchedEffect(key1 = metadata, key2 = size) {
        val newPaletteAndModel = metadata?.let {
            getVideoCoverWithPaletteAsync(context, it, size, bitmapSettings).await()
        }

        prevCoverModel = coverModel ?: newPaletteAndModel?.second
        coverModel = newPaletteAndModel?.second
        palette = newPaletteAndModel?.first
    }

    return ImageRequest.Builder(context)
        .data(coverModel)
        .prevCoverErrorOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .prevCoverFallbackOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .applyTransformations(
            context = context,
            isPlaceholderRequired = isPlaceholderRequired,
            size = size,
            isBlured = isBlured,
            usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
            prevCoverModel = prevCoverModel
        )
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .crossfade(animationMillis)
        .build() to palette
}

@Composable
fun rememberVideoCoverPainterWithPalette(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<AsyncImagePainter, Palette?> {
    val (coverModel, palette) = getVideoCoverModelWithPalette(
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        storageHandler = storageHandler,
        bitmapSettings = bitmapSettings
    )

    return rememberAsyncImagePainter(coverModel) to palette
}

@Composable
fun getTrackCoverModel(
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
            context = context,
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
    model = getTrackCoverModel(
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
fun getTrackCoverModelWithPalette(
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
            context = context,
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
    val (coverModel, palette) = getTrackCoverModelWithPalette(
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
fun getCurrentTrackCoverModel(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
): ImageRequest {
    val curTrack by storageHandler.currentTrackState.collectAsState()
    val path by remember { derivedStateOf { curTrack?.path } }

    return getTrackCoverModel(
        path = path,
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
    storageHandler: StorageHandler = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
) = rememberAsyncImagePainter(
    model = getCurrentTrackCoverModel(
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        storageHandler = storageHandler,
        bitmapSettings = bitmapSettings,
    )
)

@Composable
fun getCurrentTrackCoverModelWithPalette(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<ImageRequest, Palette?> {
    val curTrack by storageHandler.currentTrackState.collectAsState()
    val path by remember { derivedStateOf { curTrack?.path } }

    return getTrackCoverModelWithPalette(
        path = path,
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
    storageHandler: StorageHandler = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<AsyncImagePainter, Palette?> {
    val (coverModel, palette) = getCurrentTrackCoverModelWithPalette(
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        storageHandler = storageHandler,
        bitmapSettings = bitmapSettings
    )

    return rememberAsyncImagePainter(coverModel) to palette
}

private fun ImageRequest.Builder.prevCoverPlaceholder(prevCoverModel: BitmapDrawable?) =
    when (prevCoverModel) {
        null -> placeholder(R.drawable.cover_thumbnail)
        else -> placeholder(prevCoverModel)
    }

private fun ImageRequest.Builder.prevCoverError(prevCoverModel: BitmapDrawable?) =
    when (prevCoverModel) {
        null -> error(R.drawable.cover_thumbnail)
        else -> error(prevCoverModel)
    }

private fun ImageRequest.Builder.prevCoverFallback(prevCoverModel: BitmapDrawable?) =
    when (prevCoverModel) {
        null -> fallback(R.drawable.cover_thumbnail)
        else -> fallback(prevCoverModel)
    }

private fun ImageRequest.Builder.prevCoverPlaceholderOrDefault(
    usePrevCoverAsPlaceholder: Boolean,
    prevCoverModel: BitmapDrawable?,
) = when {
    usePrevCoverAsPlaceholder -> prevCoverPlaceholder(prevCoverModel)
    else -> placeholder(R.drawable.cover_thumbnail)
}

private fun ImageRequest.Builder.prevCoverErrorOrDefault(
    usePrevCoverAsPlaceholder: Boolean,
    prevCoverModel: BitmapDrawable?,
) = when {
    usePrevCoverAsPlaceholder -> prevCoverError(prevCoverModel)
    else -> error(R.drawable.cover_thumbnail)
}

private fun ImageRequest.Builder.prevCoverFallbackOrDefault(
    usePrevCoverAsPlaceholder: Boolean,
    prevCoverModel: BitmapDrawable?,
) = when {
    usePrevCoverAsPlaceholder -> prevCoverFallback(prevCoverModel)
    else -> fallback(R.drawable.cover_thumbnail)
}

private fun ImageRequest.Builder.applyTransformations(
    context: Context,
    isPlaceholderRequired: Boolean,
    size: ImageSize?,
    isBlured: Boolean,
    usePrevCoverAsPlaceholder: Boolean,
    prevCoverModel: BitmapDrawable?
) = apply {
    if (isPlaceholderRequired)
        prevCoverPlaceholderOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)

    if (isBlured)
        transformations(BlurTransformation(context))

    size?.run { size(width, height) }
}