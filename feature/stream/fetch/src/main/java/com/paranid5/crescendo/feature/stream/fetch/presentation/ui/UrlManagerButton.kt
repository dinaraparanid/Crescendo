package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.ui.foundation.AppRippleButton

private val IconSize = 18.dp

@Composable
internal fun UrlManagerButton(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = AppRippleButton(
    modifier = modifier,
    onClick = onClick,
    contentPadding = PaddingValues(vertical = dimensions.padding.medium),
) {
    Icon(
        imageVector = icon,
        contentDescription = title,
        modifier = Modifier.size(IconSize)
    )

    Spacer(Modifier.width(dimensions.padding.small))

    Text(
        text = title,
        style = AppTheme.typography.regular,
    )
}
