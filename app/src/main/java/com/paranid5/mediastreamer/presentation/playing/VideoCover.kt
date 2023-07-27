package com.paranid5.mediastreamer.presentation.playing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import coil.compose.AsyncImagePainter
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.ui.extensions.getLightVibrantOrPrimary

@Composable
internal fun VideoCover(
    coilPainter: AsyncImagePainter,
    palette: Palette?,
    modifier: Modifier = Modifier
) {
    val lightVibrantColor = palette.getLightVibrantOrPrimary()

    Image(
        painter = coilPainter,
        contentDescription = stringResource(R.string.video_cover),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1F)
            .fillMaxSize()
            .shadow(
                elevation = 80.dp,
                shape = RoundedCornerShape(5.dp),
                ambientColor = lightVibrantColor,
                spotColor = lightVibrantColor
            )
            .clip(RoundedCornerShape(20.dp)),
    )
}