package com.paranid5.crescendo.presentation.main.tracks.views.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.paranid5.crescendo.R
import com.paranid5.crescendo.media.images.ImageSize
import com.paranid5.crescendo.presentation.main.trackCoverModel

@Composable
fun TrackCover(trackPath: String, modifier: Modifier = Modifier) {
    val trackCover = trackCoverModel(
        path = trackPath,
        isPlaceholderRequired = true,
        size = ImageSize(200, 200),
        animationMillis = 250
    )

    AsyncImage(
        model = trackCover,
        contentDescription = stringResource(id = R.string.track_cover),
        alignment = Alignment.Center,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
    )
}