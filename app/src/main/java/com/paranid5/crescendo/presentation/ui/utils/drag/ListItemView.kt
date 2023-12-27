package com.paranid5.crescendo.presentation.ui.utils.drag

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias ListItemView<T> = @Composable (
    items: List<T>,
    index: Int,
    modifier: Modifier
) -> Unit