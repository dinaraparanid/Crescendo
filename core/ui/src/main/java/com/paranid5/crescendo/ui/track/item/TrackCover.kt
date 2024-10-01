package com.paranid5.crescendo.ui.track.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.ui.covers.trackCoverModel

private val AnimDurationMs = 250

@Composable
fun TrackCover(trackPath: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var trackCover by remember { mutableStateOf<ImageRequest?>(null) }

    LaunchedEffect(context, trackPath) {
        trackCover = trackCoverModel(
            context = context,
            trackPath = trackPath,
            isPlaceholderRequired = true,
            animationMillis = AnimDurationMs,
        )
    }

    AsyncImage(
        model = trackCover,
        contentDescription = stringResource(id = R.string.track_cover),
        alignment = Alignment.Center,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}