package com.paranid5.crescendo.playing.presentation.ui

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.ui.covers.currentTrackCoverModel
import com.paranid5.crescendo.ui.covers.videoCoverModel
import com.paranid5.crescendo.utils.extensions.increaseDarkness

@Composable
internal fun BackgroundImage(audioStatus: AudioStatus, modifier: Modifier = Modifier) {
    val config = LocalConfiguration.current

    val coverModel = when (audioStatus) {
        AudioStatus.STREAMING -> videoCoverModel(
            isPlaceholderRequired = true,
            size = ImageSize(config.screenWidthDp, config.screenHeightDp),
            isBlured = Build.VERSION.SDK_INT < Build.VERSION_CODES.S,
            bitmapSettings = Bitmap::increaseDarkness,
        )

        AudioStatus.PLAYING -> currentTrackCoverModel(
            isPlaceholderRequired = true,
            size = ImageSize(config.screenWidthDp, config.screenHeightDp),
            isBlured = Build.VERSION.SDK_INT < Build.VERSION_CODES.S,
            bitmapSettings = Bitmap::increaseDarkness,
        )
    }

    AsyncImage(
        model = coverModel,
        modifier = modifier.blur(radius = 15.dp),
        contentDescription = stringResource(R.string.video_cover),
        contentScale = ContentScale.FillBounds,
        alignment = Alignment.Center,
    )
}