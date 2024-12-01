package com.paranid5.crescendo.feature.meta_editor.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.ui.foundation.AppExpandableCard
import com.paranid5.crescendo.ui.foundation.AppExpandableCardStyle
import com.paranid5.crescendo.ui.foundation.AppLoadingBox
import com.paranid5.crescendo.ui.foundation.LoadingBoxSize

@Composable
internal fun MetaEditorExpandableBlock(
    icon: ImageVector,
    title: String,
    isLoading: Boolean,
    size: LoadingBoxSize,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onRetryClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) = AppLoadingBox(
    isLoading = isLoading,
    isError = isError,
    size = size,
    modifier = modifier,
    onErrorButtonClick = onRetryClick,
) {
    AppExpandableCard(
        title = title,
        modifier = Modifier.fillMaxWidth(),
        style = AppExpandableCardStyle.create(isInitiallyExpanded = true),
        prefix = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = colors.icon.primary,
                modifier = Modifier.size(dimensions.iconSize.big),
            )
        },
    ) {
        content()
    }
}