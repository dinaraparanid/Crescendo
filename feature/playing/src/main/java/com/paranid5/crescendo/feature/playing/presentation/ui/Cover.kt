package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val CoverElevation = 80.dp

@Composable
internal fun Cover(
    coverModel: ImageRequest,
    color: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(dimensions.corners.extraMedium)

    AsyncImage(
        model = coverModel,
        contentDescription = stringResource(R.string.video_cover),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = modifier
            .clip(shape)
            .simpleShadow(
                elevation = CoverElevation,
                color = color,
                shape = shape,
            ),
    )
}
