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

private val NextButtonWidth = 128.dp
private val NextButtonHeight = 64.dp

@Composable
internal fun NextButton(
    enabled: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = IconButton(
    enabled = enabled,
    modifier = modifier.simpleShadow(color = tint),
    onClick = onClick,
) {
    NextIcon(
        tint = tint,
        modifier = Modifier
            .width(NextButtonWidth)
            .height(NextButtonHeight),
    )
}

@Composable
private fun NextIcon(tint: Color, modifier: Modifier = Modifier) =
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_music_next),
        contentDescription = stringResource(R.string.ten_secs_forward),
        tint = tint,
        modifier = modifier,
    )
