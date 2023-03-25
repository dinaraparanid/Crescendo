package com.paranid5.mediastreamer.presentation.ui

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toDrawable
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.Transformation
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import org.koin.androidx.compose.get

@Composable
fun rememberVideoCoverPainter(
    isPlaceholderRequired: Boolean,
    vararg transformation: Transformation,
    storageHandler: StorageHandler = get(),
): AsyncImagePainter {
    val metadata by storageHandler.currentMetadataState.collectAsState()
    val context = LocalContext.current
    val glideUtils = GlideUtils(context)

    var coverModel by remember { mutableStateOf<Bitmap?>(null) }
    var prevCoverModel by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(key1 = metadata) {
        val newModel = metadata?.let { glideUtils.getVideoCoverAsync(it).await() }
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
                    placeholder(prevCoverModel?.toDrawable(context.resources))
            }
            .crossfade(true)
            .transformations(*transformation)
            .build()
    )
}