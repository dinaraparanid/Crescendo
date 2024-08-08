package com.paranid5.crescendo.ui.track.item.properties

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors

private val IconSize = 24.dp

@Composable
internal fun PropertyIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) = Icon(
    imageVector = icon,
    tint = colors.text.primary,
    contentDescription = null,
    modifier = modifier.size(IconSize),
)
