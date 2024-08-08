package com.paranid5.crescendo.feature.playing.presentation.ui.playback_buttons

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.utils.extensions.simpleShadow

@Composable
internal fun PauseButton(
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = IconButton(
    modifier = modifier.simpleShadow(color = tint),
    onClick = onClick,
) {
    PauseIcon(
        tint = tint,
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensions.padding.small),
    )
}

@Composable
private fun PauseIcon(tint: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(R.drawable.ic_pause),
        contentDescription = stringResource(R.string.pause),
        tint = tint,
    )
