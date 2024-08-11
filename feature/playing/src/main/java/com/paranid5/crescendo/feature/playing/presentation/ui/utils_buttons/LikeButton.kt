package com.paranid5.crescendo.feature.playing.presentation.ui.utils_buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val IconSize = 32.dp

@Composable
internal fun LikeButton(
    isLiked: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Box(modifier) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .simpleShadow(color = tint)
            .align(Alignment.Center),
    ) {
        LikeIcon(
            isLiked = isLiked,
            tint = tint,
            modifier = Modifier.size(IconSize),
        )
    }
}

@Composable
private fun LikeIcon(
    isLiked: Boolean,
    tint: Color,
    modifier: Modifier = Modifier
) = when {
    isLiked -> FilledIcon(tint = tint, modifier = modifier)
    else -> BorderIcon(tint = tint, modifier = modifier)
}

@Composable
private fun BorderIcon(tint: Color, modifier: Modifier = Modifier) = Icon(
    modifier = modifier.padding(dimensions.padding.minimum),
    imageVector = ImageVector.vectorResource(R.drawable.ic_like),
    contentDescription = stringResource(R.string.play_favourites),
    tint = tint,
)

@Composable
private fun FilledIcon(tint: Color, modifier: Modifier = Modifier) = Icon(
    modifier = modifier,
    imageVector = Icons.Filled.Favorite,
    contentDescription = stringResource(R.string.play_favourites),
    tint = tint,
)
