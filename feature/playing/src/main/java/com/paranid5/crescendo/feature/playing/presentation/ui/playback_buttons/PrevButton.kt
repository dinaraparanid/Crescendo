package com.paranid5.crescendo.feature.playing.presentation.ui.playback_buttons

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val IconWidth = 128.dp
private val IconHeight = 64.dp

@Composable
internal fun PrevButton(
    enabled: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = IconButton(
    enabled = enabled,
    onClick = onClick,
    modifier = modifier.simpleShadow(color = tint),
) {
    PrevIcon(
        tint = tint,
        modifier = Modifier
            .width(IconWidth)
            .height(IconHeight),
    )
}

@Composable
private fun PrevIcon(tint: Color, modifier: Modifier = Modifier) =
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_music_previous),
        contentDescription = stringResource(R.string.ten_secs_back),
        tint = tint,
        modifier = modifier,
    )
