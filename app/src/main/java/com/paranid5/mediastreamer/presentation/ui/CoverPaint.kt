package com.paranid5.mediastreamer.presentation.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import org.koin.compose.koinInject

@Composable
internal inline fun rememberVideoCoverPainter(
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    storageHandler: StorageHandler = koinInject(),
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
): AsyncImagePainter {
    val metadata by storageHandler.currentMetadataState.collectAsState()
    val context = LocalContext.current
    val glideUtils = GlideUtils(context)

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var prevCoverModel by remember { mutableStateOf<BitmapDrawable?>(null) }

    LaunchedEffect(metadata) {
        val newModel = metadata?.let {
            glideUtils
                .getVideoCoverAsync(it, size, bitmapSettings)
                .await()
        }

        prevCoverModel = coverModel ?: newModel
        coverModel = newModel
    }

    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(coverModel)
            .error(R.drawable.cover_thumbnail)
            .fallback(R.drawable.cover_thumbnail)
            .apply {
                if (isPlaceholderRequired)
                    placeholder(prevCoverModel)

                if (isBlured)
                    transformations(BlurTransformation(context))

                size?.run { size(first, second) }
            }
            .precision(Precision.EXACT)
            .scale(Scale.FILL)
            .crossfade(true)
            .build()
    )
}

@Composable
internal inline fun rememberVideoCoverPainterWithPalette(
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    storageHandler: StorageHandler = koinInject(),
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
): Pair<AsyncImagePainter, Palette?> {
    val metadata by storageHandler.currentMetadataState.collectAsState()
    val context = LocalContext.current
    val glideUtils = GlideUtils(context)

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var prevCoverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var palette by remember { mutableStateOf<Palette?>(null) }

    LaunchedEffect(metadata) {
        val newPaletteAndModel = metadata?.let {
            glideUtils.getVideoCoverWithPaletteAsync(it, size, bitmapSettings).await()
        }

        prevCoverModel = coverModel ?: newPaletteAndModel?.second
        coverModel = newPaletteAndModel?.second
        palette = newPaletteAndModel?.first
    }

    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(coverModel)
            .error(R.drawable.cover_thumbnail)
            .fallback(R.drawable.cover_thumbnail)
            .apply {
                if (isPlaceholderRequired)
                    placeholder(prevCoverModel)

                if (isBlured)
                    transformations(BlurTransformation(context))

                size?.run { size(first, second) }
            }
            .precision(Precision.EXACT)
            .scale(Scale.FILL)
            .crossfade(true)
            .build()
    ) to palette
}

@Composable
internal inline fun rememberTrackCoverPainter(
    path: String,
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
): AsyncImagePainter {
    val context = LocalContext.current
    val glideUtils = GlideUtils(context)

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }

    LaunchedEffect(Unit) {
        coverModel = glideUtils
            .getTrackCoverAsync(path, size, bitmapSettings)
            .await()
    }

    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(coverModel)
            .error(R.drawable.cover_thumbnail)
            .fallback(R.drawable.cover_thumbnail)
            .apply {
                if (isPlaceholderRequired)
                    placeholder(R.drawable.cover_thumbnail)

                if (isBlured)
                    transformations(BlurTransformation(context))

                size?.run { size(first, second) }
            }
            .precision(Precision.EXACT)
            .scale(Scale.FILL)
            .crossfade(true)
            .build()
    )
}

@Composable
internal inline fun rememberTrackCoverPainterWithPalette(
    path: String,
    isPlaceholderRequired: Boolean,
    size: Pair<Int, Int>? = null,
    isBlured: Boolean = false,
    crossinline bitmapSettings: (Bitmap) -> Unit = {}
): Pair<AsyncImagePainter, Palette?> {
    val context = LocalContext.current
    val glideUtils = GlideUtils(context)

    var coverModel by remember { mutableStateOf<BitmapDrawable?>(null) }
    var palette by remember { mutableStateOf<Palette?>(null) }

    LaunchedEffect(Unit) {
        val (plt, cover) = glideUtils
            .getTrackCoverWithPaletteAsync(path, size, bitmapSettings)
            .await()

        coverModel = cover
        palette = plt
    }

    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(coverModel)
            .error(R.drawable.cover_thumbnail)
            .fallback(R.drawable.cover_thumbnail)
            .apply {
                if (isPlaceholderRequired)
                    placeholder(R.drawable.cover_thumbnail)

                if (isBlured)
                    transformations(BlurTransformation(context))

                size?.run { size(first, second) }
            }
            .precision(Precision.EXACT)
            .scale(Scale.FILL)
            .crossfade(true)
            .build()
    ) to palette
}