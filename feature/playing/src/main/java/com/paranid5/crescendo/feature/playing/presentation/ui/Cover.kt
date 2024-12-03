package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions

@Composable
internal fun Cover(
    coverModel: ImageRequest?,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(dimensions.corners.extraMedium)

    Crossfade(
        targetState = coverModel,
        label = "Cover",
        modifier = modifier,
    ) { model ->
        AsyncImage(
            model = model,
            contentDescription = stringResource(R.string.video_cover),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .clip(shape),
        )
    }
}
