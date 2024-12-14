package com.paranid5.crescendo.ui.track.item.properties

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppText

@Composable
internal fun PropertyText(
    text: String,
    modifier: Modifier = Modifier,
) = AppText(
    text = text,
    modifier = modifier,
    style = typography.regular.copy(
        color = colors.text.primary,
    ),
)
