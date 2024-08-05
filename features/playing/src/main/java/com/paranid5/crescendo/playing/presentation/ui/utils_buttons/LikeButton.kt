package com.paranid5.crescendo.playing.presentation.ui.utils_buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary
import com.paranid5.crescendo.utils.extensions.simpleShadow

@Composable
internal fun LikeButton(palette: Palette?, modifier: Modifier = Modifier) {
    val paletteColor = palette.getBrightDominantOrPrimary()
    var isLiked by remember { mutableStateOf(false) }

    Box(modifier) {
        IconButton(
            onClick = { isLiked = !isLiked },
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
        ) {
            LikeIcon(
                isLiked = isLiked,
                paletteColor = paletteColor,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
private fun LikeIcon(
    isLiked: Boolean,
    paletteColor: Color,
    modifier: Modifier = Modifier
) = when {
    isLiked -> FilledIcon(tint = paletteColor, modifier = modifier)
    else -> BorderIcon(tint = paletteColor, modifier = modifier)
}

@Composable
private fun BorderIcon(tint: Color, modifier: Modifier = Modifier) = Icon(
    modifier = modifier.padding(2.dp),
    imageVector = ImageVector.vectorResource(R.drawable.like),
    contentDescription = stringResource(R.string.favourites),
    tint = tint
)

@Composable
private fun FilledIcon(tint: Color, modifier: Modifier = Modifier) = Icon(
    modifier = modifier,
    imageVector = Icons.Filled.Favorite,
    contentDescription = stringResource(R.string.favourites),
    tint = tint
)