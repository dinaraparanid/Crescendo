package com.paranid5.crescendo.playing.presentation.views

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

@Composable
internal fun Cover(
    coverModel: ImageRequest,
    palette: Palette?,
    modifier: Modifier = Modifier
) {
    val paletteColor = palette.getBrightDominantOrPrimary()

    AsyncImage(
        model = coverModel,
        contentDescription = stringResource(R.string.video_cover),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = modifier
            .shadow(
                elevation = 80.dp,
                shape = RoundedCornerShape(4.dp),
                ambientColor = paletteColor,
                spotColor = paletteColor
            )
            .clip(RoundedCornerShape(24.dp)),
    )
}