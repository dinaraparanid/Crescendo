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
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.media.images.getVideoCoverAsync
import com.paranid5.crescendo.core.media.images.getVideoCoverWithPaletteAsync
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.currentMetadataFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.compose.koinInject

@Composable
fun videoCoverModel(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    dataStoreProvider: DataStoreProvider = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
): ImageRequest {
    val context = LocalContext.current

    val metadata by dataStoreProvider
        .currentMetadataFlow
        .collectLatestAsState(initial = null)

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
    dataStoreProvider: DataStoreProvider = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
) = rememberAsyncImagePainter(
    model = videoCoverModel(
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        dataStoreProvider = dataStoreProvider,
        bitmapSettings = bitmapSettings
    )
)

@Composable
fun videoCoverModelWithPalette(
    isPlaceholderRequired: Boolean,
    size: ImageSize? = null,
    isBlured: Boolean = false,
    usePrevCoverAsPlaceholder: Boolean = true,
    animationMillis: Int = 400,
    dataStoreProvider: DataStoreProvider = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<ImageRequest, Palette?> {
    val context = LocalContext.current

    val metadata by dataStoreProvider
        .currentMetadataFlow
        .collectLatestAsState(initial = null)

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
    dataStoreProvider: DataStoreProvider = koinInject(),
    bitmapSettings: (Bitmap) -> Unit = {}
): Pair<AsyncImagePainter, Palette?> {
    val (coverModel, palette) = videoCoverModelWithPalette(
        isPlaceholderRequired = isPlaceholderRequired,
        size = size,
        isBlured = isBlured,
        usePrevCoverAsPlaceholder = usePrevCoverAsPlaceholder,
        animationMillis = animationMillis,
        dataStoreProvider = dataStoreProvider,
        bitmapSettings = bitmapSettings
    )

    return rememberAsyncImagePainter(coverModel) to palette
}