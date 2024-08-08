package com.paranid5.crescendo.ui.track.item.properties

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@Composable
internal fun PropertyText(
    text: String,
    modifier: Modifier = Modifier,
) = Text(
    text = text,
    color = colors.text.primary,
    style = typography.regular,
    modifier = modifier,
)
