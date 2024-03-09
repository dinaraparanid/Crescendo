package com.paranid5.crescendo.presentation.main.playing.views.utils_buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.utils.extensions.simpleShadow

@Composable
fun LikeButton(palette: Palette?, modifier: Modifier = Modifier) {
    val paletteColor = palette.getLightMutedOrPrimary()
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
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
private fun LikeIcon(
    isLiked: Boolean,
    paletteColor: Color,
    modifier: Modifier = Modifier
) {
    val icon by remember(isLiked) {
        derivedStateOf {
            when {
                isLiked -> R.drawable.like_filled
                else -> R.drawable.like
            }
        }
    }

    Icon(
        modifier = modifier,
        painter = painterResource(icon),
        contentDescription = stringResource(R.string.favourites),
        tint = paletteColor
    )
}