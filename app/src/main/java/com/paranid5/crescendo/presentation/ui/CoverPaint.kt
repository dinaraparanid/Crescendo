package com.paranid5.crescendo.presentation.ui

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
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.presentation.ui.utils.BlurTransformation
import com.paranid5.crescendo.presentation.ui.utils.CoilUtils
import org.koin.compose.koinInject

@Composable
internal inline fun getVideoCoverModel(
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
): ImageRequest {
    val metadata by storageHandler.currentMetadataState.collectAsState()
    val context = LocalContext.current
    val coilUtils = CoilUtils(context)

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var prevCoverModel by remember { mutableStateOf<BitmapDrawable?>(null) }

    LaunchedEffect(key1 = metadata) {
        val newModel = metadata?.let {
            coilUtils
                .getVideoCoverAsync(it, size, bitmapSettings)
                .await()
        }

        prevCoverModel = coverModel ?: newModel
        coverModel = newModel
    }

    return ImageRequest.Builder(context)
        .data(coverModel)
        .prevCoverErrorOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .prevCoverFallbackOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .apply {
            if (isPlaceholderRequired)
                prevCoverPlaceholderOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)

            if (isBlured)
                transformations(BlurTransformation(context))

            size?.run { size(first, second) }
        }
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .crossfade(animationMillis)
        .build()
}

@Composable
internal inline fun rememberVideoCoverPainter(
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
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
internal inline fun getVideoCoverModelWithPalette(
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
): Pair<ImageRequest, Palette?> {
    val metadata by storageHandler.currentMetadataState.collectAsState()
    val context = LocalContext.current
    val coilUtils = CoilUtils(context)

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var prevCoverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var palette by remember { mutableStateOf<Palette?>(null) }

    LaunchedEffect(key1 = metadata, key2 = size) {
        val newPaletteAndModel = metadata?.let {
            coilUtils.getVideoCoverWithPaletteAsync(it, size, bitmapSettings).await()
        }

        prevCoverModel = coverModel ?: newPaletteAndModel?.second
        coverModel = newPaletteAndModel?.second
        palette = newPaletteAndModel?.first
    }

    return ImageRequest.Builder(context)
        .data(coverModel)
        .prevCoverErrorOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .prevCoverFallbackOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .apply {
            if (isPlaceholderRequired)
                prevCoverPlaceholderOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)

            if (isBlured)
                transformations(BlurTransformation(context))

            size?.run { size(first, second) }
        }
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .crossfade(animationMillis)
        .build() to palette
}

@Composable
internal inline fun rememberVideoCoverPainterWithPalette(
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
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
internal inline fun getTrackCoverModel(
    path: String?,
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = false,
    animationMillis: Int = 400,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
): ImageRequest {
    val context = LocalContext.current
    val coilUtils = CoilUtils(context)

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var prevCoverModel by remember { mutableStateOf<BitmapDrawable?>(null) }

    LaunchedEffect(key1 = path, key2 = size) {
        prevCoverModel = coverModel

        coverModel = coilUtils
            .getTrackCoverAsync(path, size, bitmapSettings)
            .await()
    }

    return ImageRequest.Builder(context)
        .data(coverModel)
        .prevCoverErrorOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .prevCoverFallbackOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .apply {
            if (isPlaceholderRequired)
                prevCoverPlaceholderOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)

            if (isBlured)
                transformations(BlurTransformation(context))

            size?.run { size(first, second) }
        }
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .crossfade(animationMillis)
        .build()
}

@Composable
internal inline fun rememberTrackCoverPainter(
    path: String?,
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = false,
    animationMillis: Int = 400,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
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
internal inline fun getTrackCoverModelWithPalette(
    path: String?,
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
): Pair<ImageRequest, Palette?> {
    val context = LocalContext.current
    val coilUtils = CoilUtils(context)

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var prevCoverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var palette by remember { mutableStateOf<Palette?>(null) }

    LaunchedEffect(key1 = path, key2 = size) {
        val (plt, cover) = coilUtils
            .getTrackCoverWithPaletteAsync(path, size, bitmapSettings)
            .await()

        prevCoverModel = coverModel
        coverModel = cover
        palette = plt
    }

    return ImageRequest.Builder(context)
        .data(coverModel)
        .prevCoverErrorOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .prevCoverFallbackOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)
        .apply {
            if (isPlaceholderRequired)
                prevCoverPlaceholderOrDefault(usePrevCoverAsPlaceholder, prevCoverModel)

            if (isBlured)
                transformations(BlurTransformation(context))

            size?.run { size(first, second) }
        }
        .precision(Precision.EXACT)
        .scale(Scale.FILL)
        .crossfade(animationMillis)
        .build() to palette
}

@Composable
internal inline fun rememberTrackCoverPainterWithPalette(
    path: String?,
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
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
internal inline fun getCurrentTrackCoverModel(
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
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
internal inline fun rememberCurrentTrackCoverPainter(
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
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
internal inline fun getCurrentTrackCoverModelWithPalette(
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
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
internal inline fun rememberCurrentTrackCoverPainterWithPalette(
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    storageHandler: StorageHandler = koinInject(),
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
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