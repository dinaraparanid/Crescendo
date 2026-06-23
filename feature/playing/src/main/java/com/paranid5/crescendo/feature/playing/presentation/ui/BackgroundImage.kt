package com.paranid5.crescendo.feature.playing.presentation.ui

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.domain.image.model.ImagePath
import com.paranid5.crescendo.domain.image.model.ImageUrl
import com.paranid5.crescendo.ui.covers.trackCoverModel
import com.paranid5.crescendo.ui.covers.videoCoverModel
import com.paranid5.crescendo.utils.extensions.increaseDarkness
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun BackgroundImage(
    playbackStatus: PlaybackStatus,
    videoCovers: ImmutableList<ImageUrl>,
    trackPath: ImagePath?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val window = LocalWindowInfo.current

    var coverModel by remember { mutableStateOf<ImageRequest?>(null) }

    LaunchedEffect(context, window, playbackStatus, videoCovers, trackPath) {
        val model = when (playbackStatus) {
            PlaybackStatus.STREAMING -> videoCoverModel(
                context = context,
                videoCovers = videoCovers.map { it.value },
                isPlaceholderRequired = false,
                size = ImageSize(window.containerSize.width, window.containerSize.height),
                isBlured = Build.VERSION.SDK_INT < Build.VERSION_CODES.S,
                bitmapSettings = Bitmap::increaseDarkness,
            )

            PlaybackStatus.PLAYING -> trackCoverModel(
                context = context,
                trackPath = trackPath?.value,
                isPlaceholderRequired = false,
                size = ImageSize(window.containerSize.width, window.containerSize.height),
                isBlured = Build.VERSION.SDK_INT < Build.VERSION_CODES.S,
                bitmapSettings = Bitmap::increaseDarkness,
            )
        }

        if (model != null) coverModel = model
    }

    Crossfade(coverModel, label = "BackgroundImage") { model ->
        AsyncImage(
            model = model,
            modifier = modifier.blur(radius = dimensions.corners.small),
            contentDescription = stringResource(R.string.video_cover),
            contentScale = ContentScale.FillBounds,
            alignment = Alignment.Center,
        )
    }
}